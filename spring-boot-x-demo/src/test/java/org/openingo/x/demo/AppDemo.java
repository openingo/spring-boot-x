package org.openingo.x.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openingo.jdkits.ClassKit;
import org.openingo.jdkits.HashKit;
import org.openingo.spring.extension.data.redis.RedisTemplateX;
import org.openingo.spring.extension.data.redis.naming.DefaultKeyNamingPolicy;
import org.openingo.x.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplicationX;
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
    RedisTemplateX redisTemplateX;

    @Test
    public void ok() {
        System.out.println(redisTemplateX);
    }

    @Test
    public void testOps() {
        System.out.println(this.redisTemplateX.opsForList());
    }

    @Test
    public void testValue() {
        ValueOperations<String, String> valueOperations = this.redisTemplateX.opsForValue();
        valueOperations.set("zcq", "Qicz"+ HashKit.generateSaltForSha256());
        String zcq = valueOperations.get("zcq");
        System.out.println(zcq);
    }

    @Test
    public void testClassPackage() {
        Class<? extends SpringApplicationX> aClass = new SpringApplicationX().getClass();
        String name = aClass.getPackage().getName();
        String packageName = ClassKit.getPackageName(aClass);
        System.out.println(name);
        System.out.println(packageName);
    }

    @Test
    public void testNaming() {

        List<String> keyNames = new DefaultKeyNamingPolicy().getKeyNames(Arrays.asList("a", "b"));
        System.out.println(keyNames);
    }
}
