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
import org.openingo.spring.extension.data.redis.RedisTemplateX;
import org.openingo.spring.extension.data.redis.StringKeyRedisTemplateX;
import org.openingo.spring.extension.data.redis.StringRedisTemplateX;
import org.openingo.spring.extension.data.redis.naming.DefaultKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.openingo.spring.extension.data.redis.naming.KeyNamingKit;
import org.openingo.spring.extension.data.redis.serializer.FstRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@ConditionalOnClass({RedisTemplate.class, RedisOperations.class})
public class RedisConfig {

    /**
     * @return the default redis template
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplateX")
    public RedisTemplateX<Object, Object> redisTemplateX() {
        return new RedisTemplateX<>();
    }

    /**
     * @return the default string key redis template
     */
    @Bean
    @ConditionalOnMissingBean(name = "stringKeyRedisTemplateX")
    public StringKeyRedisTemplateX<Object> stringKeyRedisTemplateX() {
        return new StringKeyRedisTemplateX<>();
    }

    /**
     * StringRedisTemplateX
     * @return StringKeyRedisTemplateX < String, String>
     */
    @Bean
    @ConditionalOnMissingBean(name = "stringRedisTemplateX")
    public StringRedisTemplateX stringRedisTemplateX() {
        return new StringRedisTemplateX();
    }

    /**
     * redis ConnectionFactory if the cluster configured will using RedisClusterConfiguration or using RedisStandaloneConfiguration
     * @param redisProperties spring redis configurations
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        LettuceConnectionFactory connectionFactory = null;
        if (null != cluster) {
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
            Set<RedisNode> redisNodes = new HashSet<>();
            List<String> nodes = cluster.getNodes();
            Assert.notEmpty(nodes, "the cluster nodes is empty.");
            for (String node : nodes) {
                if (node == null || node.equals("")) {
                    continue;
                }
                String ip = node.split(":")[0];
                int port = Integer.parseInt(node.split(":")[1]);
                redisNodes.add(new RedisNode(ip, port));
            }
            clusterConfiguration.setClusterNodes(redisNodes);
            clusterConfiguration.setPassword(redisProperties.getPassword());
            Integer maxRedirects = cluster.getMaxRedirects();
            if (null != maxRedirects) {
                clusterConfiguration.setMaxRedirects(maxRedirects);
            }
            connectionFactory = new LettuceConnectionFactory(clusterConfiguration);
        } else {
            connectionFactory = new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        }
        return connectionFactory;
    }

    /**
     * @param redisConnectionFactory redisConnectionFactory
     * @return RedisTemplate using FST for values and StringRedisSerializer for keys
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(this.valueRedisSerializer());
        return redisTemplate;
    }

    /**
     * @return the default value serializer
     */
    @Bean
    @ConditionalOnMissingBean(name = "valueRedisSerializer")
    public <V> RedisSerializer<V> valueRedisSerializer() {
        return new FstRedisSerializer<>();
    }

    /**
     if {@code KeyNamingKit.getNaming()} is "null" return key,
     * otherwise return {@code KeyNamingKit.getNaming()}+{@code KeyNamingKit.NAMING_SEPARATOR}+key
     *
     * @see KeyNamingKit#get()
     * @return the default key naming policy
     */
    @Bean
    @ConditionalOnMissingBean
    public IKeyNamingPolicy keyNamingPolicy() {
        return new DefaultKeyNamingPolicy();
    }
}
