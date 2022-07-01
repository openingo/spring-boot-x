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

package com.izcqi;

import org.openingo.spring.boot.extension.gedid.engine.zookeeper.ZookeeperIdEngineMode;
import org.openingo.spring.boot.extension.gedid.loader.DidLoader;
import org.openingo.spring.boot.extension.gedid.loader.DidLoaderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IdLoaderConfig
 *
 * @author Qicz
 * @since 2021/6/24 16:03
 */
@Configuration
public class IdLoaderConfig implements DidLoaderConfigurer {

	@Bean
	public ZookeeperIdEngineMode zookeeperIdEngineMode() {
		return ZookeeperIdEngineMode.DATA_ZX_MID;
	}

	@Override
	public void configureDidLoader(DidLoader didLoader) {
		// business etcd-qiczz using etcd
		didLoader.follow("etcd://etcd-qiczz", 1012L);
		// business abcd using snowflake
		didLoader.follow("snowflake://abcd");
		// business abc
		didLoader.follow("redis://abc", 1012L);
		// business a
		didLoader.follow("redis", "a", 12L);
		// business b
		didLoader.follow("redis", "b", 10L);
		// business zk
		didLoader.follow("zookeeper", "zk", 1200L);
		// business uuid
		didLoader.follow("uuid", "uuid");
	}
}
