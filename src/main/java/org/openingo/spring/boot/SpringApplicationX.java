/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.lang.StrKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.boot.constants.EnvsConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * SpringApplicationX
 * 1. env
 * 2. application class
 * 3. application package
 * 4. etc
 *
 * @author Qicz
 */
@Slf4j
public final class SpringApplicationX {

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
    public static String applicationPackage = null;

    /**
     * Spring Boot Version
     */
    public static String springBootVersion = SpringBootVersion.getVersion();

    /**
     * Spring Boot Version
     */
    public static String springBootVersionX = SpringBootVersionX.getVersion();

    /**
     * whether current application running as a jar or yet
     */
    public static boolean isRunningAsJar = false;

    /**
     * config copy arg
     */
    private static final String CP_CONFIG_ARG = "ccp";

    /**
     * Provides access to the application home directory.
     */
    private static File applicationHomeSource;

    /**
     * check current spring application active profile contain 'debug/dev' or not
     * @return <tt>true</tt> if active profile is not 'prod'.
     */
    private static boolean isDebugging() {
        List<String> envs = Arrays.asList(environment.getActiveProfiles());
        return !envs.contains(EnvsConstants.PROD);
    }

    /**
     * pre init Spring Application info
     */
    private static void preInitSpringApplicationX(Class<?>[] primarySources) {
        springApplication = new SpringApplication(primarySources);
        mainApplicationClass = springApplication.getMainApplicationClass();
        applicationPackage = mainApplicationClass.getPackage().getName();
        ApplicationHome applicationHome = new ApplicationHome(mainApplicationClass);
        applicationHomeSource = applicationHome.getSource();
        if (applicationHomeSource != null) {
            String absolutePath = applicationHomeSource.getAbsolutePath();
            isRunningAsJar = absolutePath.endsWith("jar");
        }
    }

    /**
     * Init Spring Application Config
     */
    private static void initSpringApplicationX(String[] args) {
        applicationContext = springApplication.run(args);
        environment = applicationContext.getEnvironment();
        isDebugging = isDebugging();
    }

    /**
     * copy configs that in 'config' path or some 'xml','yaml','yml','properties'
     * @throws IOException io exception
     */
    private static void copyConfigsInJar() throws IOException {
        if (!isRunningAsJar) {
            return;
        }
        final String configPath = "config/";
        File config = new File(System.getProperty("user.dir") + "/" + configPath);
        if (config.exists() || !config.mkdir()) {
            System.out.println("==the configs is exist or create config path error.==");
            return;
        }
        System.out.println("==starting copy configs.==");
        JarFile jarFile = new JarFile(applicationHomeSource);
        final Set<String> configFiles = new HashSet<String>(){{
            add(".properties");
            add(".yaml");
            add(".yml");
            add(".xml");
        }};
        int copyFileCount = 0;
        for (Enumeration<? extends ZipEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            boolean isConfig = false;
            int lastIndexOf = entryName.indexOf(configPath);
            String outPath = "";
            // has config dir
            if (lastIndexOf != -1) {
                outPath = entryName.substring(lastIndexOf);
                isConfig = true;
            } else {
                lastIndexOf = entryName.lastIndexOf(".");
                if (lastIndexOf != -1) {
                    String file = entryName.substring(entryName.lastIndexOf("/") + 1);
                    boolean pomFile = "pom.properties".equals(file) || "pom.xml".equals(file);
                    isConfig = configFiles.contains(entryName.substring(lastIndexOf)) && !pomFile;
                    if (isConfig) {
                        outPath = String.join("", configPath, file);
                    }
                }
            }

            if (!isConfig) {
                continue;
            }
            InputStream jarFileInputStream = jarFile.getInputStream(entry);
            File currentFile = new File(outPath.substring(0, outPath.lastIndexOf('/')));;
            if (!currentFile.exists() && !currentFile.mkdirs()) {
                continue;
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }
            copyFileCount++;
            System.out.println("==copy \"" + entryName + "\" to \"" + outPath + "\"==");
            FileOutputStream out = new FileOutputStream(outPath);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = jarFileInputStream.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            jarFileInputStream.close();
            out.close();
        }
        System.out.println("==copy \"" + copyFileCount + "\" files.==");
        System.out.println("==copy configs finished.==");
    }

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
    @SneakyThrows
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        // pre init application X
        preInitSpringApplicationX(primarySources);
        // just cp configs
        if (ValidateKit.isNotEmpty(args) && args.length == 1 && CP_CONFIG_ARG.equals(args[0])) {
            SpringApplicationX.copyConfigsInJar();
            return null;
        }
        // init application X
        initSpringApplicationX(args);
        // log the application info
        applicationInfo();
        return applicationContext;
    }

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
     * but may also be translated into a conventional by-name lookup based on the name
     * of the given type. For more extensive retrieval operations across sets of beans,
     * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
     * @return an instance of the single bean matching the required type
     * @throws NoSuchBeanDefinitionException if no bean of the given type was found
     * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
     * @throws BeansException if the bean could not be created
     * @since 3.0
     * @see ListableBeanFactory
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * Spring application info
     */
    public static void applicationInfo() {
        String infoBuilder = "\n=======Application Info========\n" +
                String.format(" SpringBootVersion: %s\n", springBootVersion) +
                String.format(" SpringBootVersionX: %s\n", springBootVersionX) +
                String.format(" ApplicationPackage: %s\n", applicationPackage) +
                String.format(" MainApplicationClass: %s\n", mainApplicationClass.getName()) +
                String.format(" RunningAsJar: %s\n", isRunningAsJar) +
                String.format(" isDebugging: %s\n", isDebugging) +
                String.format(" Server.port: %s\n", StrKit.defaultIfBlank(environment.getProperty("server.port"), "8080")) +
                String.format(" Client.ip-address: %s\n", StrKit.defaultIfBlank(environment.getProperty("spring.cloud.client.ip-address"), "127.0.0.1")) +
                String.format(" User.dir: %s\n", environment.getProperty("user.dir")) +
                "===============================\n";
        log.info(infoBuilder);
    }
}
