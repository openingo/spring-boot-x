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

package org.openingo.spring.config;

import org.openingo.spring.constants.EnvsConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

/**
 * SpringApplicationConfig
 * 1. env
 * 2. application class
 * 3. application package
 * 4. etc
 *
 * @author Qicz
 */
public final class SpringApplicationConfig {

    private static SpringApplicationConfig springApplicationConfig;

    /**
     * @return The Spring Application Config Instance
     */
    public static SpringApplicationConfig getInstance() {
        springApplicationConfig = new SpringApplicationConfig();
        return springApplicationConfig;
    }

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
    public static ConfigurableEnvironment environment;

    /**
     * Spring Application mainApplicationClass
     */
    public static Class<?> mainApplicationClass;

    /**
     * Spring Application mainApplicationClass's package name
     */
    public static String applicationPackage;

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
     * @param springApplication
     * @param applicationContext
     */
    public static void init(SpringApplication springApplication, ConfigurableApplicationContext applicationContext) {
        SpringApplicationConfig.springApplication = springApplication;
        SpringApplicationConfig.applicationContext = applicationContext;
        SpringApplicationConfig.environment = applicationContext.getEnvironment();
        SpringApplicationConfig.isDebugging = isDebugging();
        SpringApplicationConfig.mainApplicationClass = springApplication.getMainApplicationClass();
        SpringApplicationConfig.applicationPackage = mainApplicationClass.getPackage().getName();
    }
}
