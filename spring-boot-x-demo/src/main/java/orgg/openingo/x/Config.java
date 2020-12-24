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

package orgg.openingo.x;

import com.alibaba.druid.pool.DruidDataSource;
import org.openingo.jdkits.http.RespData;
import org.openingo.spring.datasource.provider.DruidDataSourceProvider;
import org.openingo.spring.datasource.routing.RoutingDataSource;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Config
 *
 * @author Qicz
 */
@Configuration
public class Config {

    private final Environment environment;
    private final ApplicationContext applicationContext;

    public Config(Environment environment, ApplicationContext applicationContext) {
        this.environment = environment;
        this.applicationContext = applicationContext;
        System.out.println("env===="+environment);
        String endpointBasePath = environment.getProperty("management.endpoints.web.base-path");
        String address = environment.getProperty("management.server.address");
        System.out.println("basepth==="+endpointBasePath);
        System.out.println("address==="+address);
        System.out.println("env host===="+environment);
        System.out.println("env port===="+environment.getProperty("local.server.port"));
        System.out.println("env port===="+environment.getProperty("server.port"));
        System.out.println("application===="+applicationContext);
        RespData.Config.SC_KEY = "ec";
        RespData.Config.SM_KEY = "em";
        RespData.Config.FAILURE_SC = 111;
       // RespData.Config.SM_ONLY = true; // set "true" just output message
        RespData.Config.FRIENDLY_FAILURE_MESSAGE = null;//"friendly message";// set to "null" will using exception's message
    }

    @Bean
    public RedisSerializer<String> valueSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public IKeyNamingPolicy keyNamingPolicy() {
        return new KeyNamingPolicy();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties("spring.datasource")
    public DruidDataSource defaultDataSource(){
        return new DruidDataSource();
    }

    @Bean
    public RoutingDataSource routingDataSource(DruidDataSource dataSource) {
        return new RoutingDataSource(new DruidDataSourceProvider(dataSource));
    }
}
