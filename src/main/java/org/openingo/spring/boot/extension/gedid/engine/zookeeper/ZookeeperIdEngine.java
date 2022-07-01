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

package org.openingo.spring.boot.extension.gedid.engine.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.openingo.spring.boot.exception.DidException;
import org.openingo.spring.boot.extension.gedid.engine.IDidEngine;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ZookeeperIdEngine
 *
 * @author Qicz
 * @since 2021/6/25 10:41
 */
@Slf4j
public class ZookeeperIdEngine implements IDidEngine<Long> {

	private final CuratorFramework curator;
	private Long startId;
	private final ZookeeperIdEngineMode zookeeperIdEngineMode;

	public ZookeeperIdEngine(CuratorFramework curator, ZookeeperIdEngineMode zookeeperIdEngineMode) {
		this.curator = curator;
		this.zookeeperIdEngineMode = zookeeperIdEngineMode;
		log.info("starting with mode `{}`", zookeeperIdEngineMode);
	}

	@Override
	public void follow(String businessName, Long startId) {
		this.startId = startId;
		String path = this.getPath(businessName);
		try {
			Stat stat = this.curator.checkExists().forPath(path);
			if (Objects.isNull(stat)) {
				this.curator.create().forPath(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
	}

	@Override
	public String getEmbellishedName(String businessName) {
		return String.format("/%s-%s", GEDID, businessName);
	}

	@Override
	public Long getFixedStartId(Long startId) {
		if (Objects.isNull(startId)) {
			return 1L;
		}
		if (startId < 0) {
			return 1L;
		}
		return startId;
	}

	@Override
	public Long next(String businessName) {
		String path = this.getPath(businessName);
		long id = -1L;
		try {
			Stat stat = this.curator.setData().forPath(path, ("").getBytes(StandardCharsets.UTF_8));
			// default using version value
			id = stat.getVersion();
			// DATA_ZX_MID mode using (Mzxid - Czxid)
			if (ZookeeperIdEngineMode.DATA_ZX_MID.equals(this.zookeeperIdEngineMode)) {
				id =  stat.getMzxid() - stat.getCzxid();
			}
			id += this.startId;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
			throw new DidException(e.getLocalizedMessage());
		}
		return id;
	}

	@Override
	public String engineName() {
		return "zookeeper";
	}

	private String getPath(String name) {
		return this.getEmbellishedName(name);
	}

	/**
	 * will auto call when destroy
	 */
	private void close() {
		String name = this.engineName();
		log.info("the `{}` engine is closing...", name);
		this.curator.close();
		log.info("the `{}` engine is closed", name);
	}
}
