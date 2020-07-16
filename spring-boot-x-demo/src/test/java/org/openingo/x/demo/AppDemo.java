package org.openingo.x.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.extension.data.redis.RedisX;
import org.openingo.spring.extension.data.redis.naming.DefaultKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.serializer.FstSerializer;
import org.openingo.x.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

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
    RedisX<String> redisX;

    @Test
    public void ok() {
        System.out.println(redisX);
    }

    @Test
    public void testOps() {
        System.out.println(this.redisX.opsForList());
    }

    @Test
    public void testValue() {
        ValueOperations<String, String> valueOperations = this.redisX.opsForValue();
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
        byte[] zcqs = new FstSerializer<>().serialize("zcq");
        System.out.println(new String(zcqs));
    }

    @Test
    public void testVersion() {
        String springBootVersionX = SpringApplicationX.springBootVersionX;
        System.out.println(springBootVersionX);
    }
}
