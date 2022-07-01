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

package com.izcqi.controller.idempotent;

import com.izcqi.entity.UserEntity;
import org.openingo.spring.boot.extension.distributedlock.lock.DistributedLock;
import org.openingo.spring.boot.extension.idempotent.annotation.Idempotent;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;

/**
 * IdempotentController
 *
 * @author Qicz
 * @since 2021/8/9 14:44
 */
@RestController
public class IdempotentController {

	@Idempotent(keyEl = "#userEntity.id", expireMinutes = 1)
	@PostMapping("/idempotent")
	public String hello(@RequestBody UserEntity userEntity) {
		final Lock a = DistributedLock.newLock("a");
		try {
			final boolean b = a.tryLock();
			Assert.isTrue(b, "get lock error");
			return "ok => " + b;
		} finally {
			a.unlock();
		}
	}
}
