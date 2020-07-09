package org.openingo.x;

import org.openingo.spring.annotation.EnableExtension;
import org.springframework.boot.SpringApplicationX;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * App
 *
 * @author Qicz
 */
@SpringBootApplication
@EnableExtension
public class App {

    public static void main(String[] args) {
        SpringApplicationX.runX(App.class, args);
    }
}
