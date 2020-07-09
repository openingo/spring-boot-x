package org.openingo.x.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openingo.spring.extension.data.redis.RedisTemplateX;
import org.openingo.x.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * AppDemo
 *
 * @author zhucongqi
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
}
