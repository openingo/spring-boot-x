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
import org.openingo.spring.boot.extension.distributedlock.store.DistributedLockStore;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * DistributedLockPingPong
 *
 * @author Qicz
 * @since 2021/8/10 09:44
 */
@Slf4j
public class DistributedLockPingPong {

	static Map<String, DistributedLockOwner> DISTRIBUTED_LOCK_OWNERS = new ConcurrentHashMap<>();

	static DistributedLockStore DISTRIBUTED_LOCK_STORE;

	private final static Long PING_PONG_DEADLINE = 10000L;

	public static void start(DistributedLockStore distributedLockStore) {
		if (Objects.isNull(DISTRIBUTED_LOCK_STORE)) {
			DISTRIBUTED_LOCK_STORE = distributedLockStore;
		}
		Thread pingPong = new Thread() {

			@Override
			public void run() {
				log.info("lock ping pong thread running...");
				while (true) {
					try {
						LockSupport.parkUntil(SystemClockKit.now() + PING_PONG_DEADLINE);
						for (Map.Entry<String, DistributedLockOwner> entry : DISTRIBUTED_LOCK_OWNERS.entrySet()) {
							RespData respData = DISTRIBUTED_LOCK_STORE.extendedLockExpireTime(entry.getKey(), entry.getValue().getLockToken(), DistributedLockConfig.EXPIRE_SECONDS);
							log.info("DistributedReentrantLock extendedLockExpireTime {}", respData);
							if (respData.succeed()) {
								entry.getValue().setLastPingPongTimeMillis(SystemClockKit.now());
							}
						}

						for (Map.Entry<String, DistributedLockOwner> entry : DISTRIBUTED_LOCK_OWNERS.entrySet()) {
							boolean expired = (SystemClockKit.now() - entry.getValue().getLastPingPongTimeMillis()) > DistributedLockConfig.EXPIRE_SECONDS;
							if (expired) {
								DISTRIBUTED_LOCK_OWNERS.remove(entry.getKey());
								log.info("DistributedReentrantLock ping pong lock expired for resource {}", entry.getKey());
							}
						}
					} catch (Exception ex) {
						log.error("ping Pong Thread error", ex);
					}
				}
			}
		};
		pingPong.setDaemon(true);
		pingPong.start();
	}
}
