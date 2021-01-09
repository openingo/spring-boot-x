## Release Notes

### 2.9.RELEASE

- copy SpringBoot configs

```java
/**
 * App
 *
 * @author Qicz
 */
@SpringBootApplication
@EnableExtension
public class App {

    public static void main(String[] args) throws InterruptedException {
        SpringApplicationX.run(App.class, args);
        SpringApplicationX.applicationInfo();
    }
}
```

- use args `ccp` just copy application configs

```bash
java -jar xxx.jar ccp
```

### 2.5.RELEASE

- support Elasticsearch `MappingsProperties` configuration.

```java
    @PostConstruct
    private void createIndex() throws IOException {
        MappingsProperties mappingsProperties = MappingsProperties.me();
        mappingsProperties.add(MappingsProperty.me().name("id").type("long"));
        mappingsProperties.add(MappingsProperty.me().name("name").textType().analyzer("ik_smart"));
        restHighLevelClientX.createIndex("index_name", null, mappingsProperties);
    }
```

- support Elasticsearch RestHighLevelClient(X) `saveOrUpdate`(Batch) and `deleteDocById`(s).

### 2.3-RELEASE

- support Elasticsearch

### 1.9-RELEASE

- feign header cover support 
- feign hystrix concurrency strategy