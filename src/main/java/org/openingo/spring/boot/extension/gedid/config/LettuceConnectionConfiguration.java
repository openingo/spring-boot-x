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

import io.lettuce.core.RedisClient;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

/**
 * Redis connection configuration using Lettuce.
 *
 * @author Mark Paluch
 * @author Andy Wilkinson
 * @author Qicz
 */
@EnableConfigurationProperties(RedisIdEngineConfigProperties.class)
@ConditionalOnClass(RedisClient.class)
public class LettuceConnectionConfiguration extends RedisConnectionConfiguration {

	private final RedisIdEngineConfigProperties properties;

	private final ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;

	LettuceConnectionConfiguration(RedisIdEngineConfigProperties properties,
                                   ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                   ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                   ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {
		super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
		this.properties = properties;
		this.builderCustomizers = builderCustomizers;
	}

	@Override
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(DefaultClientResources.create(),
				this.properties.getLettuce().getPool());
		LettuceConnectionFactory connectionFactory = createLettuceConnectionFactory(clientConfig);
		connectionFactory.afterPropertiesSet();
		return connectionFactory;
	}

	private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
		if (getSentinelConfig() != null) {
			return new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new LettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
		}
		return new LettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

	private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources, RedisIdEngineConfigProperties.Pool pool) {
		LettuceClientConfigurationBuilder builder = createBuilder(pool);
		applyProperties(builder);
		if (StringUtils.hasText(this.properties.getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		builder.clientResources(clientResources);
		customize(builder);
		return builder.build();
	}

	private LettuceClientConfigurationBuilder createBuilder(RedisIdEngineConfigProperties.Pool pool) {
		if (pool == null) {
			return LettuceClientConfiguration.builder();
		}
		return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(pool));
	}

	private LettuceClientConfigurationBuilder applyProperties(
			LettuceClientConfigurationBuilder builder) {
		if (this.properties.isSsl()) {
			builder.useSsl();
		}
		if (this.properties.getTimeout() != null) {
			builder.commandTimeout(this.properties.getTimeout());
		}
		if (this.properties.getLettuce() != null) {
			RedisIdEngineConfigProperties.Lettuce lettuce = this.properties.getLettuce();
			if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
				builder.shutdownTimeout(this.properties.getLettuce().getShutdownTimeout());
			}
		}
		return builder;
	}

	private void customizeConfigurationFromUrl(LettuceClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	private void customize(LettuceClientConfigurationBuilder builder) {
		this.builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
	}
}
