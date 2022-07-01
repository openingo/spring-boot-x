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

package org.openingo.spring.boot.extension.distributedlock.store;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.http.RespData;
import org.openingo.spring.boot.extension.data.redis.RedisTemplateX;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Objects;

/**
 * DistributedLockStore
 *
 * @author Qicz
 * @since 2021/8/9 15:47
 */
@Slf4j
public class DistributedLockStore extends RedisTemplateX<String, String> {

	public DistributedLockStore(StringRedisTemplate stringRedisTemplate) {
		super(stringRedisTemplate);
	}

	/**
	 * lock resource
	 *
	 * @param resource lock resource
	 * @param lockToken token
	 * @param expireTimeInSecond expire time
	 */
	public RespData lock(String resource,
						 String lockToken,
						 Integer expireTimeInSecond) {
		try {
			List<Boolean> ret = this.execute(() -> {
				this.setNx(key(resource), lockToken, expireTimeInSecond);
			});
			return RespData.success(Objects.nonNull(ret) && ret.get(0));
		} catch (Throwable throwable) {
			log.error("lock error", throwable);
			return RespData.failure("failure");
		}
	}

	/**
	 * unlock resource
	 *
	 * @param resource lock resource
	 * @param lockToken token
	 */
	public RespData unlock(String resource, String lockToken) {
		final String token = this.get(key(resource));
		if (!Objects.equals(token, lockToken)) {
			return RespData.failure("unlock failure");
		}
		try {
			final Boolean ret = this.del(key(resource));
			if (Objects.nonNull(ret) && ret) {
				return RespData.success();
			}
			return RespData.failure("unlock failure");
		} catch (Throwable throwable) {
			log.error("unlock error", throwable);
			return RespData.failure("unlock failure");
		}
	}

	/**
	 * extended Lock Expire Time
	 *
	 * @param resource lock resource
	 * @param lockToken token
	 * @param expireTimeInSecond expire time
	 */
	public RespData extendedLockExpireTime(String resource,
										   String lockToken,
										   Integer expireTimeInSecond) {
		final String token = this.get(key(resource));
		if (!Objects.equals(token, lockToken)) {
			return RespData.failure("extended lock failure");
		}
		try {
			this.setEx(key(resource), expireTimeInSecond, lockToken);
			return RespData.success();
		} catch (Throwable throwable) {
			log.error("extended lock error", throwable);
			return RespData.failure("extended lock failure");
		}
	}

	private String key(String resource) {
		return String.format("Distributed-Locks:%s", resource);
	}
}
