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

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.openingo.spring.boot.extension.gedid.engine.etcd.EtcdIdEngine;
import org.openingo.spring.boot.extension.gedid.engine.redis.RedisIdEngine;
import org.openingo.spring.boot.extension.gedid.engine.snowflake.SnowflakeIdEngine;
import org.openingo.spring.boot.extension.gedid.engine.uuid.UuidEngine;
import org.openingo.spring.boot.extension.gedid.engine.zookeeper.ZookeeperIdEngine;
import org.openingo.spring.boot.extension.gedid.engine.zookeeper.ZookeeperIdEngineMode;
import org.openingo.spring.boot.extension.gedid.loader.DidLoader;
import org.openingo.spring.boot.extension.gedid.loader.DidLoaderConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;

/**
 * GeDidConfig
 *
 * @author Qicz
 * @since 2021/6/23 10:24
 */
@Configuration
@Import(DidLoaderConfiguration.class)
public class GeDidConfig {

	@Bean
	DidLoader didLoader() {
		return new DidLoader();
	}

	@Configuration
	@ConditionalOnClass({ RedisOperations.class, RedisClient.class })
	@Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
	static class RedisIdEngineConfig {

		@Bean
		RedisIdEngine redisIdEngine(RedisConnectionConfiguration configuration) {
			StringRedisTemplate template = new StringRedisTemplate();
			template.setConnectionFactory(configuration.redisConnectionFactory());
			template.afterPropertiesSet();
			return new RedisIdEngine(template);
		}
	}

	@Slf4j
	@Configuration
	@ConditionalOnClass({ CuratorFramework.class, RetryPolicy.class, ZookeeperAutoConfiguration.class})
	@EnableConfigurationProperties(ZookeeperIdEngineConfigProperties.class)
	static class ZookeeperIdEngineConfig {

		@Bean(destroyMethod = "close")
		ZookeeperIdEngine zookeeperIdEngine(ZookeeperIdEngineConfigProperties properties, ZookeeperIdEngineMode mode) {
			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
			builder.connectString(properties.getConnectString());
			RetryPolicy retryPolicy = this.exponentialBackoffRetry(properties);
			CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
			curator.start();
			String connectString = properties.getConnectString();
			String name = ZookeeperIdEngine.class.getSimpleName();
			log.info("`{}` blocking until connected to zookeeper at {} for " + properties.getBlockUntilConnectedWait() + properties.getBlockUntilConnectedUnit(), name, connectString);
			try {
				curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}
			log.info("`{}` connected to zookeeper at {}", name, connectString);
			return new ZookeeperIdEngine(curator, mode);
		}

		private RetryPolicy exponentialBackoffRetry(ZookeeperIdEngineConfigProperties properties) {
			return new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(), properties.getMaxRetries(), properties.getMaxSleepMs());
		}

		@Bean
		@ConditionalOnMissingBean
		public ZookeeperIdEngineMode zookeeperIdEngineMode() {
			return ZookeeperIdEngineMode.DATA_VERSION;
		}
	}

	@Configuration
	static class UuidEngineConfig {

		@Bean
		UuidEngine uuidEngine() {
			return new UuidEngine();
		}
	}

	@Configuration
	static class SnowflakeConfig {

		@Bean
		@ConditionalOnMissingBean
		SnowflakeIdEngine snowflakeIdEngine() {
			return new SnowflakeIdEngine();
		}
	}

	@Configuration
	@ConditionalOnClass({ Client.class })
	@EnableConfigurationProperties(EtcdIdEngineConfigProperties.class)
	static class EtcdConfig {

		@Bean(destroyMethod = "close")
		EtcdIdEngine etcdIdEngine(EtcdIdEngineConfigProperties properties) {
			ClientBuilder clientBuilder = Client.builder().endpoints(properties.getEndpoints().toArray(new String[0]));
			String user = properties.getUser();
			if (StringUtils.isNotBlank(user)) {
				clientBuilder.user(ByteSequence.from(user.getBytes(StandardCharsets.UTF_8)));
			}
			String password = properties.getPassword();
			if (StringUtils.isNotBlank(password)) {
				clientBuilder.password(ByteSequence.from(password.getBytes(StandardCharsets.UTF_8)));
			}
			Client etcdClient = clientBuilder.build();
			return new EtcdIdEngine(etcdClient);
		}
	}
}
