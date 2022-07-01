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

package org.openingo.spring.boot.extension.distributedlock.lock;

import lombok.Builder;
import lombok.Data;

/**
 * DistributedLockOwner
 *
 * @author Qicz
 * @since 2021/8/9 17:26
 */
@Data
@Builder
class DistributedLockOwner {

	/**
	 * lock thread
	 */
	private Thread thread;

	/**
	 * lock token
	 */
	private String lockToken;

	/**
	 * locked count
	 */
	private Integer lockedCount;

	/**
	 * last ping pong time
	 */
	private Long lastPingPongTimeMillis;

	/**
	 * update lock reference
	 * @param count current update count : 1 or -1
	 */
	public void reference(Integer count) {
		lockedCount += count;
	}
}
