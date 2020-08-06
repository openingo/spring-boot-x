package org.openingo.x.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openingo.jdkits.collection.ListKit;
import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.extension.data.redis.StringKeyRedisTemplateX;
import org.openingo.spring.extension.data.redis.naming.DefaultKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.serializer.FstRedisSerializer;
import org.openingo.x.App;
import org.openingo.x.datasource.IDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * AppDemo
 *
 * @author Qicz
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = App.class)
public class AppDemo {

    @Autowired
    StringKeyRedisTemplateX<String> stringKeyRedisTemplateX;

    @Test
    public void ok() {
        System.out.println(stringKeyRedisTemplateX);
    }

    @Test
    public void testOps() {
        System.out.println(this.stringKeyRedisTemplateX.opsForList());
    }

    @Test
    public void testValue() {
        ValueOperations<String, String> valueOperations = this.stringKeyRedisTemplateX.opsForValue();
        valueOperations.set("zcq", "Qicz");
        valueOperations.append("zcq", "123");
        String zcq = valueOperations.get("zcq");
        System.out.println(zcq);
    }

    @Test
    public void testNaming() {

        List<String> keyNames = new DefaultKeyNamingPolicy().getKeyNames(Arrays.asList("a", "b"));
        System.out.println(keyNames);
    }

    @Test
    public void testFst() {
        byte[] zcqs = new FstRedisSerializer<>().serialize("zcq");
        System.out.println(new String(zcqs));
    }

    @Test
    public void testVersion() {
        String springBootVersionX = SpringApplicationX.springBootVersionX;
        System.out.println(springBootVersionX);
    }

    @Autowired
    IDataSourceService iDataSourceService;

    @Test
    public void testMultiThreadRoutingDataSources() throws IOException {
        int size = 3;
        List<String> dataSourceNames = ListKit.emptyArrayList(size);
        for (int i = 0; i < size; i++) {
            dataSourceNames.add("ds"+i);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < size; i++) {
            String name = dataSourceNames.get(i);
            executorService.submit(() -> {
                System.out.println("thread-add======"+Thread.currentThread().getName());
                iDataSourceService.add(name);
            });
        }
        for (int i = 0; i < size; i++) {
            String name = dataSourceNames.get(i);
            executorService.submit(() -> {
                System.out.println("thread-sw======"+Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                    iDataSourceService.switchDataSource(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();

        System.in.read();
    }

}
