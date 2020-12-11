## Release Notes

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