## spring-boot-x

> spring-boot-extensions

![maven](https://img.shields.io/maven-central/v/org.openingo.spring/spring-boot-x.svg)


### [Release Notes](RELEASENOTES.md)

### Features

- Elasticsearch supports 

  - the `RestHighLevelClientX` extension for `RestHighLevelClient`, simplification for doc operations:`put`,`delete`,`search`.
  - the `ElasticsearchTemplateLite` extension for `ElasticsearchTemplate`
  - base on ElasticsearchTemplate `7.2.0` and dependency jars(rest client & rest high level client)
  
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

- custom `ErrorAttributes`, with handler, include exception information:

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

- `RoutingDataSource` (dynamic dataSource)

  - support Hikaricp & Alibaba Druid

  - how?

    ```java
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("spring.datasource")
    public DruidDataSource defaultDataSource(){
        return new DruidDataSource();
    }
    
    @Bean
    public RoutingDataSource routingDataSource(DruidDataSource dataSource) {
        return new RoutingDataSource(new DruidDataSourceProvider(dataSource));
    }
    ```

    ```java
    /**
     * DataSourceService
     *
     * @author Qicz
     */
    @Service
    @Slf4j
    public class DataSourceService implements IDataSourceService {
    
        @Autowired
        RoutingDataSource routingDataSource;
    
        @Autowired
        DruidDataSource dataSource;
    
        @Override
        public void switchDataSource(String name) throws SQLException {
            try {
                System.out.println("======before======"+name);
                routingDataSource.getConnection();
                System.out.println(routingDataSource.getCurrentUsingDataSourceProvider().hashCode());
                RoutingDataSourceHolder.setCurrentUsingDataSourceKey(name);
                routingDataSource.getConnection();
                System.out.println("======after======");
                System.out.println(routingDataSource.getCurrentUsingDataSourceProvider().hashCode());
            } finally {
                RoutingDataSourceHolder.clearCurrentUsingDataSourceKey();
            }
        }
    
        @Override
        public void add(String name) {
            //routingDataSource.setAutoCloseSameKeyDataSource(false);
            DruidDataSourceProvider druidDataSourceProvider = new DruidDataSourceProvider(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            druidDataSourceProvider.startProviding();
            routingDataSource.addDataSource(name, druidDataSourceProvider);
        }
    }
    ```

    > `RoutingDataSourceHolder.getCurrentUsingDataSourceKey` will get and remove current using.

- validate groups[TODO]

- others [TODO]

### How to use?

- Dependency:

  ```xml
  <dependency>
    <groupId>org.openingo.spring</groupId>
    <artifactId>spring-boot-x</artifactId>
    <version>new_version</version>
  </dependency>
  ```

- Add `@EnableExtension` in your main application class:

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

- Configuration:

  ```yml
  openingo:
    redis:
      enable: true
    http:
      request:
        cors:
          allowed-header: "*"
          enable: true
          allowed-all: true
        log:
          enable: true
        error:
          enable: true
  ```

- `RespData.Config`

  ```java
  /**
   * Config
   *
   * @author Qicz
   */
  @Configuration
  public class Config {
  
      public Config() {
          RespData.Config.SC_KEY = "ec";
          RespData.Config.SM_KEY = "em";
          RespData.Config.FAILURE_SC = 111;
          //RespData.Config.SM_ONLY = true; // set "true" just output message
          RespData.Config.FRIENDLY_FAILURE_MESSAGE = null;//"friendly message";// set to "null" will using exception's message
      }
  }  
  ```

  - controller

    ```java
    @GetMapping("/ex1")
    public RespData ex1(){
        throw new IndexOutOfBoundsException("IndexOutOfBoundsException message");
    }
    
    @GetMapping("/ex2")
    public RespData ex2() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.readValue("[{asd}]", Map.class);
        return RespData.success();
    }
    ```

  - `/ex1`output

    ```json
    {
        "em": "IndexOutOfBoundsException message",
        "ec": 123
    }
    ```

  - `/ex1`output with friendly message, `RespData.Config.FRIENDLY_FAILURE_MESSAGE = "friendly message";`

    ```json
    {
        "em": "friendly message",
        "ec": 123
    }
    ```

    - log.error information

      ```bash
      2020-07-13 23:56:00.572 ERROR 35387 --- [nio-8080-exec-2] o.s.b.w.s.error.DefaultErrorAttributesX  : 
      ****************************************************************
      :: SpringApplicationX :: for current request report information 
      ****************************************************************
      
      2020-07-13 23:56:00.587 ERROR 35387 --- [nio-8080-exec-2] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.IndexOutOfBoundsException: IndexOutOfBoundsException message] with root cause
      
      java.lang.IndexOutOfBoundsException: IndexOutOfBoundsException message
      	at org.openingo.x.controller.UserController.ex1(UserController.java:97) ~[classes/:na]
      ```

  - `/ex2`output

    ```json
    {
        "em": "Cannot deserialize instance of `java.util.LinkedHashMap` out of START_ARRAY token\n at [Source: (String)\"[{asd}]\"; line: 1, column: 1]",
        "ec": 345
    }
    ```

  - `/ex2`output with friendly message, `RespData.Config.FRIENDLY_FAILURE_MESSAGE = "friendly message";`

    ```json
    {
        "em": "friendly message",
        "ec": 345
    }
    ```

    - log.error information

      ```bash
      2020-07-14 00:11:15.424 ERROR 35599 --- [nio-8080-exec-2] o.s.b.w.s.error.DefaultErrorAttributesX  : 
      ****************************************************************
      :: SpringApplicationX :: for current request report information 
      ****************************************************************
      
      2020-07-14 00:11:15.436 ERROR 35599 --- [nio-8080-exec-2] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception
      
      com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize instance of `java.util.LinkedHashMap` out of START_ARRAY token
       at [Source: (String)"[{asd}]"; line: 1, column: 1]
      	at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:63) ~[jackson-databind-2.9.9.3.jar:2.9.9.3]
      ```

- Custom exception error code in your business:

  ```java
  /**
   * BusinessErrorAttributes
   *
   * @author Qicz
   */
  @Component
  public class BusinessErrorAttributes extends DefaultServiceErrorAttributes {
  
      /**
       * Decorate exception error code, custom for your business logic.
       * <code>
       * <pre>
       * public Object decorateExceptionCode(Exception exception) {
       *    if (exception instanceof IndexOutOfBoundsException) {
       *      return 123;
       *    }
       *   return super.decorateExceptionCode(exception);
       * }
       * </pre>
       * </code>
       *
       * @param exception the exception that got thrown during handler execution
       */
      @Override
      public Object decorateExceptionCode(Exception exception) {
          if (exception instanceof IndexOutOfBoundsException) {
              return 123;
          }
          return super.decorateExceptionCode(exception);
      }
  }
  ```
  
- [Demo](./spring-boot-x-demo)

