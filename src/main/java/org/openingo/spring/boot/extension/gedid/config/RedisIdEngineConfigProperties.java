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

import java.time.Duration;
import java.util.List;

/**
 * RedisIdEngineConfigProperties
 *
 * @author Qicz
 * @since 2021/6/30 11:00
 */
@ConfigurationProperties(prefix = PropertiesConstants.GEDID_ENGINE_REDIS_CONFIG_PROPERTIES_PREFIX)
public class RedisIdEngineConfigProperties {

	/**
	 * Database index used by the connection factory.
	 */
	private int database = 0;

	/**
	 * Connection URL. Overrides host, port, and password. User is ignored. Example:
	 * redis://user:password@example.com:6379
	 */
	private String url;

	/**
	 * Redis server host.
	 */
	private String host = "localhost";

	/**
	 * Login password of the redis server.
	 */
	private String password;

	/**
	 * Redis server port.
	 */
	private int port = 6379;

	/**
	 * Whether to enable SSL support.
	 */
	private boolean ssl;

	/**
	 * Connection timeout.
	 */
	private Duration timeout;

	private Sentinel sentinel;

	private Cluster cluster;

	private final Jedis jedis = new Jedis();

	private final Lettuce lettuce = new Lettuce();

	public int getDatabase() {
		return this.database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return this.ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	public Duration getTimeout() {
		return this.timeout;
	}

	public Sentinel getSentinel() {
		return this.sentinel;
	}

	public void setSentinel(Sentinel sentinel) {
		this.sentinel = sentinel;
	}

	public Cluster getCluster() {
		return this.cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Jedis getJedis() {
		return this.jedis;
	}

	public Lettuce getLettuce() {
		return this.lettuce;
	}

	/**
	 * Pool properties.
	 */
	public static class Pool {

		/**
		 * Maximum number of "idle" connections in the pool. Use a negative value to
		 * indicate an unlimited number of idle connections.
		 */
		private int maxIdle = 8;

		/**
		 * Target for the minimum number of idle connections to maintain in the pool. This
		 * setting only has an effect if both it and time between eviction runs are
		 * positive.
		 */
		private int minIdle = 0;

		/**
		 * Maximum number of connections that can be allocated by the pool at a given
		 * time. Use a negative value for no limit.
		 */
		private int maxActive = 8;

		/**
		 * Maximum amount of time a connection allocation should block before throwing an
		 * exception when the pool is exhausted. Use a negative value to block
		 * indefinitely.
		 */
		private Duration maxWait = Duration.ofMillis(-1);

		/**
		 * Time between runs of the idle object evictor thread. When positive, the idle
		 * object evictor thread starts, otherwise no idle object eviction is performed.
		 */
		private Duration timeBetweenEvictionRuns;

		public int getMaxIdle() {
			return this.maxIdle;
		}

		public void setMaxIdle(int maxIdle) {
			this.maxIdle = maxIdle;
		}

		public int getMinIdle() {
			return this.minIdle;
		}

		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		public int getMaxActive() {
			return this.maxActive;
		}

		public void setMaxActive(int maxActive) {
			this.maxActive = maxActive;
		}

		public Duration getMaxWait() {
			return this.maxWait;
		}

		public void setMaxWait(Duration maxWait) {
			this.maxWait = maxWait;
		}

		public Duration getTimeBetweenEvictionRuns() {
			return this.timeBetweenEvictionRuns;
		}

		public void setTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
			this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
		}

	}

	/**
	 * Cluster properties.
	 */
	public static class Cluster {

		/**
		 * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
		 * "initial" list of cluster nodes and is required to have at least one entry.
		 */
		private List<String> nodes;

		/**
		 * Maximum number of redirects to follow when executing commands across the
		 * cluster.
		 */
		private Integer maxRedirects;

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public Integer getMaxRedirects() {
			return this.maxRedirects;
		}

		public void setMaxRedirects(Integer maxRedirects) {
			this.maxRedirects = maxRedirects;
		}

	}

	/**
	 * Redis sentinel properties.
	 */
	public static class Sentinel {

		/**
		 * Name of the Redis server.
		 */
		private String master;

		/**
		 * Comma-separated list of "host:port" pairs.
		 */
		private List<String> nodes;

		public String getMaster() {
			return this.master;
		}

		public void setMaster(String master) {
			this.master = master;
		}

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

	}

	/**
	 * Jedis client properties.
	 */
	public static class Jedis {

		/**
		 * Jedis pool configuration.
		 */
		private Pool pool;

		public Pool getPool() {
			return this.pool;
		}

		public void setPool(Pool pool) {
			this.pool = pool;
		}

	}

	/**
	 * Lettuce client properties.
	 */
	public static class Lettuce {

		/**
		 * Shutdown timeout.
		 */
		private Duration shutdownTimeout = Duration.ofMillis(100);

		/**
		 * Lettuce pool configuration.
		 */
		private Pool pool;

		public Duration getShutdownTimeout() {
			return this.shutdownTimeout;
		}

		public void setShutdownTimeout(Duration shutdownTimeout) {
			this.shutdownTimeout = shutdownTimeout;
		}

		public Pool getPool() {
			return this.pool;
		}

		public void setPool(Pool pool) {
			this.pool = pool;
		}

	}
}
