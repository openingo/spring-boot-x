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

package org.openingo.spring.boot.extension.datasource.routing;

import lombok.extern.slf4j.Slf4j;
import org.openingo.java.lang.ThreadLocalX;
import org.openingo.jdkits.lang.ThreadShare;
import org.openingo.jdkits.lang.ThreadShareKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.boot.extension.datasource.holder.RoutingDataSourceHolder;
import org.openingo.spring.boot.extension.datasource.provider.IDataSourceProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RoutingDataSource
 *
 * {@link DataSource} implementation that routes {@link #getConnection()}
 * calls to one of various target DataSources based on a lookup key. The latter is usually
 * (but not necessarily) determined through some thread-bound transaction context.
 *
 * @author Qicz
 * @see #setTargetDataSources
 * @see #setDefaultTargetDataSource
 * @see #determineCurrentLookupKey()
 */
@Slf4j
public class RoutingDataSource extends AbstractDataSource implements DataSource, InitializingBean {

	/**
	 * current using dataSource provider
	 */
	private final static String CURRENT_USING_DATA_SOURCE = "currentUsingDataSourceProvider";

	@Nullable
	private ConcurrentHashMap<Object, IDataSourceProvider> targetDataSources;

	@Nullable
	private IDataSourceProvider defaultTargetDataSource;

	/**
	 *  auto close the same key dataSource
	 */
	private Boolean autoCloseSameKeyDataSource = true;

	public RoutingDataSource() {
		this.targetDataSources = new ConcurrentHashMap<>();
	}

	public RoutingDataSource(IDataSourceProvider defaultTargetDataSource) {
		this();
		this.defaultTargetDataSource = defaultTargetDataSource;
		ThreadShareKit.put(CURRENT_USING_DATA_SOURCE, defaultTargetDataSource);
	}

	public RoutingDataSource(IDataSourceProvider defaultTargetDataSource, ConcurrentHashMap<Object, IDataSourceProvider> targetDataSources) {
		this(defaultTargetDataSource);
		this.targetDataSources = targetDataSources;
	}

	/**
	 * whether or not close the dataSource that has the same dataSource key
	 * @param autoCloseSameKeyDataSource <tt>true</tt> close the dataSource that has the same dataSourceKey
	 */
	public void setAutoCloseSameKeyDataSource(Boolean autoCloseSameKeyDataSource) {
		this.autoCloseSameKeyDataSource = autoCloseSameKeyDataSource;
	}

	/**
	 * the dataSource has the {@code dataSourceKey} is exists or not.
	 * @param dataSourceKey dataSource key
	 * @return <tt>true</tt> exists
	 */
	public Boolean hasDataSource(Object dataSourceKey) {
		Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
		return this.targetDataSources.containsKey(dataSourceKey);
	}

	/**
	 * add a new dataSource
	 * @param dataSourceKey dataSource key
	 * @param dataSource dataSource Provider
	 */
	public void addDataSource(Object dataSourceKey, IDataSourceProvider dataSource) {
		Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
		Assert.notNull(dataSourceKey, "[Assertion failed] - the dataSourceKey argument cannot be null");
		Assert.notNull(dataSource, "[Assertion failed] - the dataSource argument cannot be null");
		if (this.hasDataSource(dataSourceKey)) {
			log.info("The dataSource with the same key \"{}\" is exists.", dataSourceKey);
			Assert.isTrue(this.autoCloseSameKeyDataSource, "the dataSource ["+dataSourceKey+"] with the same key that is exists. you can set the 'autoCloseSameKeyDataSource' to true to auto close the same dataSource.");
			IDataSourceProvider closingDataSource = this.targetDataSources.get(dataSourceKey);
			if (closingDataSource.destroy()) {
				log.info("The dataSource with the key \"{}\" is closed", dataSourceKey);
				this.targetDataSources.remove(dataSourceKey);
			} else {
				log.error("The dataSource with the key \"{}\" has an error in close operations", dataSourceKey);
			}
		}
		this.targetDataSources.put(dataSourceKey, dataSource);
		log.info("The dataSource with the key \"{} \" is added.", dataSourceKey);
	}

	/**
	 * add more dataSources
	 * @param dataSources dataSource provider mapping
	 */
	public void addDataSources(Map<Object, IDataSourceProvider> dataSources) {
		if (ValidateKit.isNull(dataSources)) {
			return;
		}

		for(Map.Entry<Object, IDataSourceProvider> entry: dataSources.entrySet()) {
			this.addDataSource(entry.getKey(), entry.getValue());
		}
		log.info("All dataSources being added have been successfully added.");
	}

	/**
	 * remove the dataSource with the {@code dataSourceKey}
	 * @param dataSourceKey dataSource key dataSource key
	 */
	public void removeDataSource(Object dataSourceKey) {
		Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
		if (this.hasDataSource(dataSourceKey)) {
			IDataSourceProvider deletingDataSource = this.targetDataSources.remove(dataSourceKey);
			if (ValidateKit.isNotNull(deletingDataSource)) {
				deletingDataSource.destroy();
			}
			log.info("The dataSource with the key \"{}\" is removed.", dataSourceKey);
		}
	}

	@PreDestroy
	public void preDestroy() {
		if (ValidateKit.isNotNull(this.targetDataSources)) {
			for (Map.Entry<Object, IDataSourceProvider> entry : this.targetDataSources.entrySet()) {
				entry.getValue().destroy();
				log.info("The dataSource with the key \"{}\" is closed.", entry.getKey());
			}
			this.targetDataSources.clear();
		}
	}

	/**
	 * Specify the map of target DataSources, with the lookup key as key.
	 * The mapped value can either be a corresponding {@link DataSource}
	 * instance.
	 * {@link #determineCurrentLookupKey()}.
	 */
	public void setTargetDataSources(ConcurrentHashMap<Object, IDataSourceProvider> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	/**
	 * Specify the default target DataSource, if any.
	 * <p>The mapped value can either be a corresponding {@link DataSource}
	 * instance.
	 * {@link #determineCurrentLookupKey()} current lookup key.
	 */
	public void setDefaultTargetDataSource(IDataSourceProvider defaultTargetDataSource) {
		this.defaultTargetDataSource = defaultTargetDataSource;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.defaultTargetDataSource == null) {
			throw new IllegalArgumentException("Property 'defaultTargetDataSource' is required");
		}
		if (this.targetDataSources == null) {
			throw new IllegalArgumentException("Property 'targetDataSources' is required");
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this)) {
			return (T) this;
		}
		return determineTargetDataSource().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
	}

	/**
	 * Retrieve the current target DataSource. Determines the
	 * {@link #determineCurrentLookupKey() current lookup key}, performs
	 * a lookup in the {@link #setTargetDataSources targetDataSources} map,
	 * falls back to the specified
	 * {@link #setDefaultTargetDataSource default target DataSource} if necessary.
	 * @see #determineCurrentLookupKey()
	 */
	protected DataSource determineTargetDataSource() {
		Assert.notNull(this.targetDataSources, "DataSource router not initialized");
		IDataSourceProvider dataSourceProvider = this.defaultTargetDataSource;
		Object lookupKey = determineCurrentLookupKey();
		if (lookupKey != null) {
			dataSourceProvider = this.targetDataSources.get(lookupKey);
		}
		if (dataSourceProvider == null) {
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
		ThreadShareKit.put(CURRENT_USING_DATA_SOURCE, dataSourceProvider);
		return dataSourceProvider.getDataSource();
	}

	/**
	 * Determine the current lookup key.
	 */
	@Nullable
	protected Object determineCurrentLookupKey() {
		return RoutingDataSourceHolder.getCurrentUsingDataSourceKey();
	}

	/**
	 * Returns current using dataSource provider instance
	 * default is defaultTargetDataSource
	 * @return dataSource Instance
	 */
	public IDataSourceProvider getCurrentUsingDataSourceProvider() {
		return ThreadShareKit.get(CURRENT_USING_DATA_SOURCE);
	}
}
