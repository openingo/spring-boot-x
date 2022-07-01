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

package org.openingo.spring.boot.extension.gedid.engine.redis;

import org.openingo.spring.boot.extension.data.redis.RedisTemplateX;
import org.openingo.spring.boot.extension.gedid.engine.IDidEngine;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * RedisIdEngine
 *
 * @author Qicz
 * @since 2021/6/24 15:28
 */
public class RedisIdEngine extends RedisTemplateX<String, String> implements IDidEngine<Long> {

	public RedisIdEngine(StringRedisTemplate stringRedisTemplate) {
		super(stringRedisTemplate);
	}

	@Override
	public void follow(String businessName, Long startId) {
		startId -= 1L;
		// if the key (this.name) cannot exist, create and set the value with `startId`.<br/>
		// if the key (this.name) is existed , no operation is performed.
		this.setNx(this.getEmbellishedName(businessName), startId.toString());
	}

	@Override
	public String getEmbellishedName(String businessName) {
		return String.format("%s:%s", GEDID, businessName);
	}

	@Override
	public Long getFixedStartId(Long startId) {
		if (Objects.isNull(startId)) {
			return 1L;
		}
		if (startId < 0) {
			startId = 1L;
		}
		return startId;
	}

	@Override
	public Long next(String businessName) {
		return this.incr(this.getEmbellishedName(businessName));
	}

	@Override
	public String engineName() {
		return "redis";
	}
}
