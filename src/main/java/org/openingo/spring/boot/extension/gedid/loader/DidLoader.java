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

package org.openingo.spring.boot.extension.gedid.loader;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.safety.Safety;
import org.openingo.spring.boot.exception.DidException;
import org.openingo.spring.boot.extension.gedid.engine.IDidEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DidLoader
 *
 * @author Qicz
 * @since 2021/6/24 15:18
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class DidLoader {

	private final Safety safety = new Safety();

	private final Map<String, IDidEngine> engineMapping = new HashMap<>();

	private final Map<String, IDidEngine> businessEngineMapping = new HashMap<>();

	protected void addEngine(IDidEngine<?> engine) {
		String engineName = engine.engineName();
		if (!this.engineMapping.containsKey(engineName)) {
			this.engineMapping.put(engineName, engine);
		}
	}

	/**
	 * follow one business with uri
	 *
	 * @param businessUri the business uri, eg: redis://business
	 */
	public void follow(String businessUri) {
		this.follow(this.getUri(businessUri), null);
	}

	/**
	 * follow one business with businessUri
	 *
	 * @param businessUri the business businessUri, eg: redis://business
	 * @param startId the first id
	 */
	public <T> void follow(String businessUri, T startId) {
		this.follow(this.getUri(businessUri), startId);
	}

	/**
	 * follow one business with businessUri
	 *
	 * @param businessUri the business url, eg: redis://business
	 */
	public <T> void follow(URI businessUri) {
		this.follow(businessUri.getScheme(), businessUri.getHost());
	}

	/**
	 * follow one business with businessUri
	 *
	 * @param businessUri the business url, eg: redis://business
	 * @param startId the first id
	 */
	public <T> void follow(URI businessUri, T startId) {
		this.follow(businessUri.getScheme(), businessUri.getHost(), startId);
	}

	/**
	 * follow on business with engine name and business name
	 * @param engineName the engine name, eg: redis, zookeeper, uuid
	 * @param businessName the business name
	 */
	public void follow(String engineName, String businessName) {
		this.follow(engineName, businessName, null);
	}

	/**
	 * follow on business with engine name and business name
	 * @param engineName the engine name, eg: redis, zookeeper, uuid
	 * @param businessName the business name
	 * @param startId the first id
	 */
	public <T> void follow(String engineName, String businessName, T startId) {
		if (!this.engineMapping.containsKey(engineName)) {
			log.info("the engine `{}` is not exist.", engineName);
			return;
		}
		IDidEngine engine = this.engineMapping.get(engineName);
		// check the business is followed or not
		if (this.businessEngineMapping.containsKey(businessName)) {
			String message = String.format("the engine `%s` is following this business `%s`", this.businessEngineMapping.get(businessName).engineName(), businessName);
			throw new DidException(message);
		}
		// mapping business and engine
		this.businessEngineMapping.put(businessName, engine);
		T fixedStartId = (T)engine.getFixedStartId(startId);
		this.safety.safetyRun(() -> engine.follow(businessName, fixedStartId));
		log.info("`{}` engine following the business `{}` and startId is `{}`", engineName, businessName, fixedStartId);
	}

	/**
	 * convert next to Long
	 * @param businessName the business name
	 * @return the next long id
	 */
	public Long nextToLong(String businessName) {
		return (Long)this.next(businessName);
	}

	/**
	 * convert next to String
	 * @param businessName the business name
	 * @return the next String id
	 */
	public String nextToString(String businessName) {
		return this.next(businessName).toString();
	}

	/**
	 * fetch the next id for business name
	 * @param businessName the business name
	 * @param <T>
	 * @return the next id
	 */
	public <T> T next(String businessName) {
		IDidEngine engine = this.businessEngineMapping.get(businessName);
		if (Objects.isNull(engine)) {
			String message = String.format("the engine for business `%s` is not exist.", businessName);
			throw new DidException(message);
		}
		T nextId = this.safety.safetyCall(() -> (T)engine.next(businessName));
		log.info("`{}` engine following the business `{}` and the nextId is `{}`", engine.engineName(), businessName, nextId);
		return nextId;
	}

	private URI getUri(String uriString) {
		URI uri = null;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.error("the uri `{}` is malformed", uriString);
		}
		return uri;
	}
}
