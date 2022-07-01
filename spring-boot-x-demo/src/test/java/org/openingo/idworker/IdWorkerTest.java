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

package org.openingo.idworker;

import com.izcqi.App;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openingo.spring.boot.extension.gedid.loader.DidLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RedisIdWorkerTest
 *
 * @author Qicz
 * @since 2021/6/24 15:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = App.class)
public class IdWorkerTest {

	@Autowired
	DidLoader didLoader;

	private void testIdWorkers(String name) throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(7);
		for (int i = 0; i < 100; i++) {
			executorService.submit(() -> {
				Long next = didLoader.next(name);
				String ret = String.format("thread name %s, name = %s, next = %s",
						Thread.currentThread().getName(), name, next);
				System.out.println(ret);
			});
		}
		System.in.read();
	}

	@Test
	public void testRedisIdWorker() throws IOException {
		this.testIdWorkers("a");
	}

	@Test
	public void testZkIdWorker() throws IOException {
		this.testIdWorkers("zk");
	}

	@Test
	public void testSnowflakeIdWorker() throws IOException {
		this.testIdWorkers("abcd");
	}

	@Test
	public void testEtcdIdWorker() throws IOException {
		this.testIdWorkers("etcd-qiczz");
	}

	@Autowired
	private CuratorFramework curator;

	private Set<Integer> ids = new HashSet<>();

	@SneakyThrows
	@Test
	public void testZkId() {
		String path = "/zcqqq1";
		Stat stat = this.curator.checkExists().forPath(path);

		if (Objects.isNull(stat)) {
			this.curator.create().forPath(path);
		}

		ExecutorService executorService = Executors.newFixedThreadPool(7);
		for (int i = 0; i < 100; i++) {
			executorService.submit(() -> {
				try {
					Stat stat1 = this.curator.setData().forPath(path, ("").getBytes(StandardCharsets.UTF_8));
					int version = stat1.getVersion();
					if (ids.contains(version)) {
						System.out.println("hhhhhhh");
					}
					ids.add(version);
					System.out.println("ret ===> id : " + stat1.getMzxid() + " version :" + version + " sub :" + (stat1.getMzxid() - stat1.getCzxid()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		System.in.read();
	}
}
