package feign.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.Feign;
import feign.RequestLine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.net.URISyntaxException;

public class SetterFactoryTest {

  interface TestInterface {
    @RequestLine("POST /")
    String invoke();

    @RequestLine("POST /")
    String invoke(URI uri);
  }

  @Rule
  public final ExpectedException thrown = ExpectedException.none();
  @Rule
  public final MockWebServer server = new MockWebServer();

  @Test
  public void customSetter() {
    thrown.expect(HystrixRuntimeException.class);
    thrown.expectMessage("POST / failed and no fallback available.");

    server.enqueue(new MockResponse().setResponseCode(500));

    SetterFactory commandKeyIsRequestLine = (target, method) -> {
      String groupKey = target.name();
      String commandKey = method.getAnnotation(RequestLine.class).value();
      return HystrixCommand.Setter
          .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
          .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
    };

    TestInterface api = HystrixFeign.builder()
        .setterFactory(commandKeyIsRequestLine)
        .target(TestInterface.class, "http://localhost:" + server.getPort());

    api.invoke();
  }

  @Test
  public void customInvocationRuntimeSetter() throws URISyntaxException {
    thrown.expect(HystrixRuntimeException.class);
    thrown.expectMessage("POST / failed and no fallback available.");

    server.enqueue(new MockResponse().setResponseCode(500));

    InvocationRuntimeSetterFactory commandKeyIsRequestLine = (target, proxy, method, args) -> {
      String groupKey = target.name();
      String commandKey = Feign.configKey(target.type(), method);
      Object anyUri = null;
      if(args != null && args.length > 0) {
        for (Object arg : args) {
          if(arg instanceof URI) {
            anyUri = arg;
          }
        }
      }

      if (anyUri != null) {
        URI uri = (URI) anyUri;
        groupKey = groupKey + "-" + uri.toString();
        commandKey = commandKey + "-" + uri.toString();
      }
      return HystrixCommand.Setter
              .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
              .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
    };

    TestInterface api = HystrixFeign.builder()
            .invocationRuntimeSetterFactory(commandKeyIsRequestLine)
            .target(TestInterface.class, "test");

    api.invoke(new URI("http://localhost:" + server.getPort()));
  }
}
