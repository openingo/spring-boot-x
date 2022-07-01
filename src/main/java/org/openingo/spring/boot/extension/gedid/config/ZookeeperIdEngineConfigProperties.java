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

package org.openingo.spring.boot.extension.gedid.config;

import org.openingo.spring.boot.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * ZookeeperIdEngineConfigProperties
 *
 * @author Qicz
 * @since 2021/6/30 10:57
 */
@Validated
@ConfigurationProperties(prefix = PropertiesConstants.GEDID_ENGINE_ZOOKEEPER_CONFIG_PROPERTIES_PREFIX)
public class ZookeeperIdEngineConfigProperties {

	/**
	 * Connection string to the Zookeeper cluster.
	 */
	@NotNull
	private String connectString = "localhost:2181";

	/**
	 * Is Zookeeper enabled.
	 */
	private boolean enabled = true;

	/**
	 * Initial amount of time to wait between retries.
	 */
	private Integer baseSleepTimeMs = 50;

	/**
	 * Max number of times to retry.
	 */
	private Integer maxRetries = 10;

	/**
	 * Max time in ms to sleep on each retry.
	 */
	private Integer maxSleepMs = 500;

	/**
	 * Wait time to block on connection to Zookeeper.
	 */
	private Integer blockUntilConnectedWait = 10;

	/**
	 * The unit of time related to blocking on connection to Zookeeper.
	 */
	private TimeUnit blockUntilConnectedUnit = TimeUnit.SECONDS;

	public String getConnectString() {
		return this.connectString;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Integer getBaseSleepTimeMs() {
		return this.baseSleepTimeMs;
	}

	public Integer getMaxRetries() {
		return this.maxRetries;
	}

	public Integer getMaxSleepMs() {
		return this.maxSleepMs;
	}

	public Integer getBlockUntilConnectedWait() {
		return this.blockUntilConnectedWait;
	}

	public TimeUnit getBlockUntilConnectedUnit() {
		return this.blockUntilConnectedUnit;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setBaseSleepTimeMs(Integer baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void setMaxSleepMs(Integer maxSleepMs) {
		this.maxSleepMs = maxSleepMs;
	}

	public void setBlockUntilConnectedWait(Integer blockUntilConnectedWait) {
		this.blockUntilConnectedWait = blockUntilConnectedWait;
	}

	public void setBlockUntilConnectedUnit(TimeUnit blockUntilConnectedUnit) {
		this.blockUntilConnectedUnit = blockUntilConnectedUnit;
	}
}
