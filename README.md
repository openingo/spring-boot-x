## spring-boot-x

> spring-boot-extensions

![maven](https://img.shields.io/maven-central/v/org.openingo.spring/spring-boot-x.svg)

### Features

- request log.

  ```bas
  --------------------------------------------------------------------------------
  :: SpringApplicationX :: Request Report 
  Client IP  : 127.0.0.1 
  Controller  : org.openingo.x.controller.UserController.(UserController.java:1)
  URI  : http://localhost:8080/user1/zcz 
  Handler(Action)  : user1
  Method  : GET
  Request Time  : 2020-07-11T18:13:58.764 
  Processing Time  : 0.063s
  Header(s)  : [user-agent:"PostmanRuntime/7.25.0", accept:"*/*", cache-control:"no-cache", postman-token:"50e5a856-3068-4094-867e-844df8e7c98f", host:"localhost:8080", accept-encoding:"gzip, deflate, br", connection:"keep-alive", content-length:"21", Content-Type:"application/json;charset=UTF-8"]
  Body  : {"name":"qicz"}
  Response  : ok
  --------------------------------------------------------------------------------
  ```

- redis template extension, add key naming policy.

- TODO

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

- demo

  https://github.com/OpeningO/spring-boot-x/tree/master/spring-boot-x-demo

