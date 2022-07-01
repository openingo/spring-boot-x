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

package com.izcqi.etcd.demo;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TestEtcd
 *
 * @author Qicz
 * @since 2021/7/2 18:11
 */
public class TestEtcd {

	@Test
	public void testOne() throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(6);
		// create client
		Client client = Client.builder().endpoints("http://localhost:2379", "http://localhost:2379").build();
		KV kvClient = client.getKVClient();
		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {

				ByteSequence key = ByteSequence.from("qq".getBytes());
				ByteSequence value = ByteSequence.from("".getBytes());

// put the key-value
				try {
					PutResponse putResponse = kvClient.put(key, value, PutOption.newBuilder().withLeaseId(0L).withPrevKV().build()).get();


					System.out.println("======================");
					long version = putResponse.getPrevKv().getVersion() + 1;
					System.out.println("version=="+version);
					System.out.println("======================");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		executorService.shutdown();
		System.in.read();
	}
}
