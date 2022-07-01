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

import org.openingo.spring.boot.extension.gedid.engine.IDidEngine;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * DidLoaderConfiguration
 *
 * @author Qicz
 * @since 2021/7/1 10:56
 */
@SuppressWarnings("rawtypes")
public class DidLoaderConfiguration implements ApplicationContextAware {

	private final DidLoader didLoader;

	public DidLoaderConfiguration(DidLoader didLoader) {
		this.didLoader = didLoader;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, IDidEngine> engineMapping = applicationContext.getBeansOfType(IDidEngine.class);
		engineMapping.values().forEach(this.didLoader::addEngine);
		Map<String, DidLoaderConfigurer> configurerMapping = applicationContext.getBeansOfType(DidLoaderConfigurer.class);
		configurerMapping.values().forEach(configurer -> configurer.configureDidLoader(this.didLoader));
	}
}
