# 项目背景
  本项目是对 [feign hystrix](https://github.com/OpenFeign/feign/tree/master/hystrix) 进行扩展，为解决 FeignClient 在支持动态 url 场景，一个url不可用，所有url都熔断问题。
# 解决方案
  根据运行时参数，动态设置 commandKey，从而实现熔断隔离。
# 快速开始
  你可以扩展 InvocationRuntimeSetterFactory 接口提供的方法，自定义 Hystrix Command 属性
```
  TestInterface api = HystrixFeign.builder()
             .invocationRuntimeSetterFactory(invocationRuntimeSetterFactory)
             .target(TestInterface.class, "test");
```