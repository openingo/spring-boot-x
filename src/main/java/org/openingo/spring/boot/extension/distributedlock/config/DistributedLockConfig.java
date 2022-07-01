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

package org.openingo.spring.boot.extension.distributedlock.config;

import lombok.extern.slf4j.Slf4j;
import org.openingo.spring.boot.extension.distributedlock.lock.DistributedLockPingPong;
import org.openingo.spring.boot.extension.distributedlock.store.DistributedLockStore;
import org.openingo.spring.boot.extension.gedid.config.RedisConnectionConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.locks.ReentrantLock;

/**
 * DistributedLockConfig
 *
 * @author Qicz
 * @since 2021/8/9 16:01
 */
@Slf4j
@Configuration
@ConditionalOnClass({ RedisTemplate.class, RedisOperations.class })
public class DistributedLockConfig {

	private ReentrantLock pingPongLock = new ReentrantLock();

	private boolean pingPongStarted = false;

	public final static Integer EXPIRE_SECONDS = 3 * 60 * 1000;

	public final static int MAX_TRYING_TIME_MILLIS = 5 * 60 * 1000;

	DistributedLockConfig(RedisConnectionConfiguration configuration) {
		start(configuration);
	}

	private void start(RedisConnectionConfiguration configuration) {
		this.pingPongLock.lock();
		try {
			if (this.pingPongStarted) {
				return;
			}
			StringRedisTemplate template = new StringRedisTemplate();
			template.setConnectionFactory(configuration.redisConnectionFactory());
			template.afterPropertiesSet();
			final DistributedLockStore lockStore = new DistributedLockStore(template);
			DistributedLockPingPong.start(lockStore);
			this.pingPongStarted = true;
		} finally {
			this.pingPongLock.unlock();
		}
	}
}
