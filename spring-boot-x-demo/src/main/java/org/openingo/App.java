package org.openingo;

import org.openingo.jdkits.http.RespData;
import org.openingo.spring.annotation.EnableExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * App
 *
 * @author Qicz
 * @since 2021/4/21 14:21
 */
@SpringBootApplication
@EnableExtension
public class App {

    public static void main(String[] args) {
        RespData.Config.FRIENDLY_FAILURE_MESSAGE = null;
        SpringApplication.run(App.class, args);
    }
}
