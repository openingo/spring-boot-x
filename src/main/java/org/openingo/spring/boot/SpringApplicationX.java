/*
 * MIT License
 *
 * Copyright (c) 2020 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.openingo.spring.boot;

import org.openingo.spring.constants.EnvsConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

/**
 * SpringApplicationX
 * 1. env
 * 2. application class
 * 3. application package
 * 4. etc
 *
 * @author Qicz
 */
public final class SpringApplicationX {

    /**
     * Static helper that can be used to run a {@link SpringApplication} from the
     * specified source using default settings.
     * @param primarySource the primary source to load
     * @param args the application arguments (usually passed from a Java main method)
     * @return the running {@link ApplicationContext}
     */
    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    /**
     * Static helper that can be used to run a {@link SpringApplication} from the
     * specified sources using default settings and user supplied arguments.
     * @param primarySources the primary sources to load
     * @param args the application arguments (usually passed from a Java main method)
     * @return the running {@link ApplicationContext}
     */
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        SpringApplicationX.springApplication = new SpringApplication(primarySources);
        SpringApplicationX.applicationContext = SpringApplicationX.springApplication.run(args);
        SpringApplicationX.springApplicationX = new SpringApplicationX();
        // init application X
        initSpringApplicationX();
        return applicationContext;
    }

    /**
     * Current Spring ApplicationX
     */
    public static SpringApplicationX springApplicationX;

    /**
     * Current Spring Application
     */
    public static SpringApplication springApplication;

    /**
     * Current Spring Application's applicationContext
     */
    public static ConfigurableApplicationContext applicationContext;

    /**
     * Spring Application debugging status
     */
    public static boolean isDebugging = false;

    /**
     * Spring Application environment
     * {@linkplain ConfigurableEnvironment}
     */
    public static ConfigurableEnvironment environment = null;

    /**
     * Spring Application mainApplicationClass
     */
    public static Class<?> mainApplicationClass = null;

    /**
     * Spring Application mainApplicationClass's package name
     */
    public static String applicationPackage = "org.opening.x";

    /**
     * Spring Boot Version
     */
    public static String springBootVersion = SpringBootVersion.getVersion();

    /**
     * Spring Boot Version
     */
    public static String springBootVersionX = SpringBootVersionX.getVersion();

    /**
     * check current spring application active profile contain 'debug/dev' or not
     * @return <tt>true</tt> if active profile is not 'prod'.
     */
    private static boolean isDebugging() {
        List<String> envs = Arrays.asList(environment.getActiveProfiles());
        return !envs.contains(EnvsConstants.PROD);
    }

    /**
     * Init Spring Application Config
     */
    private static void initSpringApplicationX() {
        SpringApplicationX.environment = applicationContext.getEnvironment();
        SpringApplicationX.isDebugging = isDebugging();
        SpringApplicationX.mainApplicationClass = springApplication.getMainApplicationClass();
        SpringApplicationX.applicationPackage = mainApplicationClass.getPackage().getName();
    }

    /**
     * Spring application info
     */
    public static void applicationInfo() {
        System.out.println(applicationPackage);
    }
}
