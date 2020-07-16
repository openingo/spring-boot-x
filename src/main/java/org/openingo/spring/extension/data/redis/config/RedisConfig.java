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

package org.openingo.spring.extension.data.redis.config;

import org.openingo.spring.constants.Constants;
import org.openingo.spring.constants.PropertiesConstants;
import org.openingo.spring.extension.data.redis.RedisX;
import org.openingo.spring.extension.data.redis.StringRedisX;
import org.openingo.spring.extension.data.redis.naming.DefaultKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.naming.KeyNamingKit;
import org.openingo.spring.extension.data.redis.serializer.FstRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.net.UnknownHostException;

/**
 * RedisConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnProperty(
        prefix = PropertiesConstants.REDIS_CONFIG_PROPERTIES_PREFIX,
        name = PropertiesConstants.ENABLE,
        havingValue = Constants.TRUE
)
@ConditionalOnClass(RedisTemplate.class)
public class RedisConfig {

    /**
     * @return the default redis template
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisX")
    public RedisX<Object> redisX() {
        return new RedisX<>();
    }

    /**
     * StringRedisX
     * @return RedisX < String, String>
     */
    @Bean
    @ConditionalOnMissingBean(name = "stringRedisX")
    public StringRedisX stringRedisX() {
        return new StringRedisX();
    }

    /**
     * @param redisConnectionFactory
     * @return RedisTemplate using FST for values and StringRedisSerializer for keys
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }

    /**
     * @return the default value serializer
     */
    @Bean
    @ConditionalOnMissingBean
    public <V> RedisSerializer<V> fstRedisSerializer() {
        return new FstRedisSerializer<>();
    }

    /**
     if {@code KeyNamingKit.getNaming()} is "null" return key,
     * otherwise return {@code KeyNamingKit.getNaming()}+{@code KeyNamingKit.NAMING_SEPARATOR}+key
     *
     * @see {@linkplain KeyNamingKit#getNaming()}
     * @return the default key naming policy
     */
    @Bean
    @ConditionalOnMissingBean
    public IKeyNamingPolicy keyNamingPolicy() {
        return new DefaultKeyNamingPolicy();
    }
}
