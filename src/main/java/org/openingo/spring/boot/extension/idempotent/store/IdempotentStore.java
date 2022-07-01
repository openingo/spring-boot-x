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

package org.openingo.spring.boot.extension.idempotent.store;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.spring.boot.extension.data.redis.RedisTemplateX;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * IdempotentStore
 *
 * @author Qicz
 * @since 2021/8/10 13:29
 */
@Slf4j
public class IdempotentStore extends RedisTemplateX<String, String> {

	public IdempotentStore(StringRedisTemplate stringRedisTemplate) {
		super(stringRedisTemplate);
	}

	public Object getData(String idempotentKey, Class<?> dataType) throws Throwable {
		final String key = key(idempotentKey);
		log.info("get data key {}", key);
		final String data = this.get(key);
		return JacksonKit.toObj(data, dataType);
	}

	public void saveData(String idempotentKey, Object data, Long expireMinutes) throws Throwable {
		final String key = key(idempotentKey);
		log.info("save data key {} expireMinutes {}", key, expireMinutes);
		this.setNx(key, JacksonKit.toJson(data), expireMinutes * 1000);
	}

	private String key(String idempotentKey) {
		return String.format("Idempotent-Data:%s", idempotentKey);
	}

}
