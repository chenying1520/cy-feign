# 项目背景
  本项目是对 [feign hystrix](https://github.com/OpenFeign/feign/tree/master/hystrix) 进行扩展，为解决 FeignClient 在支持动态 url 场景，一个url不可用，所有url都熔断问题。
  本包支持 9.5.0
# 解决方案
  根据运行时参数，动态设置 commandKey，从而实现熔断隔离。
# 快速开始
  你可以扩展 InvocationRuntimeSetterFactory 接口提供的方法，自定义 Hystrix Command 属性
  
 依赖引入：
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-hystrix</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>cy-feign-hystrix</artifactId>
    <version>9.5.0</version>
</dependency>
```
 使用：
```
TestInterface api = HystrixFeign.builder()
         .invocationRuntimeSetterFactory(invocationRuntimeSetterFactory)
         .target(TestInterface.class, "test");
```