## spring-boot-x

> spring-boot-extensions

![maven](https://img.shields.io/maven-central/v/org.openingo.spring/spring-boot-x.svg)

### Features

- request log:

  ```bash
  ****************************************************************
  :: SpringApplicationX :: for current request report information 
  ****************************************************************
  Client IP  : 127.0.0.1 
  Request Time  : 2020-07-12T19:24:40.249 
  Controller  : org.openingo.x.controller.UserController.(UserController.java:1)
  URI  : http://localhost:8080/json 
  Handler(Action)  : json
  Method  : GET
  Processing Time  : 0.002s
  Header(s)  : [user-agent:"PostmanRuntime/7.25.0", cache-control:"no-cache", postman-token:"b29a0616-7e31-4150-b022-4bf3680bf771", host:"localhost:8080", accept-encoding:"gzip, deflate, br", connection:"keep-alive", content-length:"21", Content-Type:"application/json;charset=UTF-8"]
  Body  : {"name":"qicz"}
  Response  : {"name":"qicz","age":18}
  ----------------------------------------------------------------
  ```

- redis template extension, add key naming policy.

- custom `ErrorAttributes`:

  > add current request handler and exception 

  ```json
  {
      "timestamp": "2020-07-13T05:49:06.071+0000",
      "status": 500,
      "error": "Internal Server Error",
      "exception": "org.openingo.spring.exception.ServiceException",
      "message": "testing exception",
      "path": "/ex",
      "handler": "public java.util.Map org.openingo.x.controller.UserController.ex()",
      "openingo.error": {
          "ex": "org.openingo.spring.exception.ServiceException: testing exception",
          "em": "testing exception",
          "error": "Internal Server Error",
          "ec": "ERROR_CODE"
      }
  }
  ```

- `SpringApplicationX` more extensions

- others [TODO]

### How to use?

- dependency

  ```xml
  <dependency>
    <groupId>org.openingo.spring</groupId>
    <artifactId>spring-boot-x</artifactId>
    <version>new_version</version>
  </dependency>
  ```

- add `@EnableExtension` in your main application class

```java
@SpringBootApplication
@EnableExtension
public class App {

    public static void main(String[] args) {
        SpringApplicationX.run(App.class, args);
        SpringApplicationX.applicationInfo();
    }
}
```

- configuration

  ```yml
  openingo:
    request:
      log:
        enable: true
    redis:
      enable: true
  ```

- ErrorAttributes

  ```java
  public class DemoErrorAttributes extends DefaultErrorAttributesX {
  
      /**
       * Create a new {@link DefaultErrorAttributesX} instance that included the
       * "exception" attribute , can not get the "exception" instance and response status.
       */
      public DemoErrorAttributes() {
          super(true, true);
      }
  
      @Override
      public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
          Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
          Map<String, Object> errorExAttributes = new HashMap<>();
          errorExAttributes.put(RespData.Config.SM_KEY, errorAttributes.get("message"));
          errorExAttributes.put("error", errorAttributes.get("error"));
          Integer status = this.getStatus();
          if (HttpStatus.OK.value() != status) {
              Object scKey = status;
              Exception ex = this.getHandlerExecutionException();
              // check ex instanceof in your application
              if (ValidateKit.isNotNull(ex)) {
                  errorExAttributes.put("ex", ex.toString());
                  if (ex instanceof ServiceException) {
                      scKey = ((ServiceException) ex).getExceptionCode();
                  }
              }
              errorExAttributes.put(RespData.Config.SC_KEY, scKey);
          }
          errorAttributes.put("openingo.error", errorExAttributes);
          return errorAttributes;
      }
  }
  ```

- demo

  https://github.com/OpeningO/spring-boot-x/tree/master/spring-boot-x-demo

