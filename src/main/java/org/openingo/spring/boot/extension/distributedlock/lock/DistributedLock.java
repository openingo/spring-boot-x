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

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.http.RespData;
import org.openingo.jdkits.sys.SystemClockKit;
import org.openingo.spring.boot.extension.distributedlock.config.DistributedLockConfig;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * DistributedLock
 *
 * @author Qicz
 * @since 2021/8/9 15:19
 */
@Slf4j
public class DistributedLock implements Lock {

	private String lockToken;
	private final String resource;
	private final boolean reentrant;

	private final static Integer LOCK_DEADLINE = 200;

	private DistributedLock(String resource, boolean reentrant) {
		this.resource = resource;
		this.reentrant = reentrant;
	}

	public static Lock newLock(String resource) {
		return new DistributedLock(resource, true);
	}

	public static Lock newLock(String resource, boolean reentrant) {
		return new DistributedLock(resource, reentrant);
	}

	@Override
	public void lock() {
		final boolean locked = this.tryLock();
		Assert.isTrue(locked, "get lock failure, try again later");
	}

	@Override
	@Deprecated
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean tryLock() {
		if (Objects.nonNull(this.lockToken)) {
			return false;
		}
		String token = UUID.randomUUID().toString();
		RespData respData = DistributedLockPingPong.DISTRIBUTED_LOCK_STORE.lock(this.resource, token, DistributedLockConfig.EXPIRE_SECONDS);
		final boolean succeed = respData.succeed();
		if (succeed && Boolean.TRUE.equals(respData.getData())) {
			addLock(token);
			return true;
		}
		final boolean needUpdate = succeed && Boolean.FALSE.equals(respData.getData()) && this.reentrant && isSameThread();
		if (needUpdate) {
			updateLock();
			return true;
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		Assert.isTrue(unit.toMillis(time) <= DistributedLockConfig.MAX_TRYING_TIME_MILLIS, "Too many attempts, just max try " + DistributedLockConfig.MAX_TRYING_TIME_MILLIS + " millis");
		long tryingLockTime = SystemClockKit.now() + unit.toMillis(time);
		log.info("trying lock time {}", tryingLockTime);
		while (SystemClockKit.now() < tryingLockTime) {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			if (tryLock()) {
				return true;
			}
			LockSupport.parkUntil(this,SystemClockKit.now() + LOCK_DEADLINE);
		}
		return false;
	}

	@Override
	public void unlock() {
		DistributedLockOwner lockOwner = DistributedLockPingPong.DISTRIBUTED_LOCK_OWNERS.get(this.resource);
		if (lockOwner != null && lockOwner.getLockToken().equals(this.lockToken)) {
			lockOwner.reference(-1);
			this.lockToken = null;
			if (lockOwner.getLockedCount() <= 0) {
				DistributedLockPingPong.DISTRIBUTED_LOCK_OWNERS.remove(this.resource);
				RespData respData = DistributedLockPingPong.DISTRIBUTED_LOCK_STORE.unlock(this.resource, lockOwner.getLockToken());
				if (!respData.succeed()) {
					log.info("unlock failed resource {}", this.resource);
				}
			}
		}
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}

	private boolean isSameThread() {
		DistributedLockOwner lockOwner =  DistributedLockPingPong.DISTRIBUTED_LOCK_OWNERS.get(this.resource);
		return Objects.nonNull(lockOwner) && lockOwner.getThread() == Thread.currentThread();
	}

	private void addLock(String lockToken) {
		this.lockToken = lockToken;
		final DistributedLockOwner lockOwner = DistributedLockOwner.builder()
				.thread(Thread.currentThread())
				.lockedCount(1)
				.lockToken(lockToken)
				.lastPingPongTimeMillis(SystemClockKit.now())
				.build();
		DistributedLockPingPong.DISTRIBUTED_LOCK_OWNERS.put(this.resource, lockOwner);
	}

	private void updateLock() {
		DistributedLockOwner lockOwner = DistributedLockPingPong.DISTRIBUTED_LOCK_OWNERS.get(this.resource);
		if (Objects.nonNull(lockOwner)) {
			this.lockToken = lockOwner.getLockToken();
			lockOwner.reference(1);
		}
	}
}
