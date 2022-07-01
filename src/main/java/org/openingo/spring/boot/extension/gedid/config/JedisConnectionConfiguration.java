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

package org.openingo.spring.boot.extension.gedid.config;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.time.Duration;

/**
 * Redis connection configuration using Jedis.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 * @author Qicz
 */
@EnableConfigurationProperties(RedisIdEngineConfigProperties.class)
@ConditionalOnClass({ GenericObjectPool.class, JedisConnection.class, Jedis.class })
public class JedisConnectionConfiguration extends RedisConnectionConfiguration {

	private final RedisIdEngineConfigProperties properties;

	private final ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers;

	JedisConnectionConfiguration(RedisIdEngineConfigProperties properties,
                                 ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                 ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                 ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
		super(properties, sentinelConfiguration, clusterConfiguration);
		this.properties = properties;
		this.builderCustomizers = builderCustomizers;
	}

	@Override
	public JedisConnectionFactory redisConnectionFactory() {
		return createJedisConnectionFactory();
	}

	private JedisConnectionFactory createJedisConnectionFactory() {
		JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
		if (getSentinelConfig() != null) {
			return new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
		}
		return new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

	private JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
		RedisIdEngineConfigProperties.Pool pool = this.properties.getJedis().getPool();
		if (pool != null) {
			applyPooling(pool, builder);
		}
		if (StringUtils.hasText(this.properties.getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		customize(builder);
		return builder.build();
	}

	private JedisClientConfigurationBuilder applyProperties(JedisClientConfigurationBuilder builder) {
		if (this.properties.isSsl()) {
			builder.useSsl();
		}
		if (this.properties.getTimeout() != null) {
			Duration timeout = this.properties.getTimeout();
			builder.readTimeout(timeout).connectTimeout(timeout);
		}
		return builder;
	}

	private void applyPooling(RedisIdEngineConfigProperties.Pool pool,
                              JedisClientConfigurationBuilder builder) {
		builder.usePooling().poolConfig(this.getPoolConfig(pool));
	}

	private void customizeConfigurationFromUrl(JedisClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	private void customize(JedisClientConfigurationBuilder builder) {
		this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
	}
}
