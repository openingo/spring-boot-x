/*
 * MIT License
 *
 * Copyright (c) 2020 OpeningO Co.,Ltd.
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

package org.springframework.jdbc.datasource.lookup;

import org.openingo.jdkits.validate.ValidateKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import javax.sql.DataSourceX;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractRoutingDataSourceX
 *
 * @author Qicz
 */
public abstract class AbstractRoutingDataSourceX<DS extends DataSourceX> extends AbstractDataSource implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRoutingDataSourceX.class);

    @Nullable
    private ConcurrentHashMap<Object, DS> targetDataSources;

    @Nullable
    private DS defaultTargetDataSource;

    private boolean lenientFallback = true;

    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    @Nullable
    private Map<Object, DataSource> resolvedDataSources;

    @Nullable
    private DataSource resolvedDefaultDataSource;

    private static final Object refreshLock = new Object();

    // auto close the same key dataSource
    private Boolean autoCloseSameKeyDataSource = true;

    public AbstractRoutingDataSourceX(DS defaultTargetDataSource, ConcurrentHashMap<Object, DS> targetDataSources) {
        this.targetDataSources = targetDataSources;
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    public void setAutoCloseSameKeyDataSource(Boolean autoCloseSameKeyDataSource) {
        this.autoCloseSameKeyDataSource = autoCloseSameKeyDataSource;
    }

    public Boolean hasDataSource(Object dataSourceKey) {
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        return this.targetDataSources.containsKey(dataSourceKey);
    }

    private void addDataSource(Object dataSourceKey, DS dataSource, boolean refresh) throws SQLException {
        Assert.notNull(dataSourceKey, "[Assertion failed] - the dataSourceKey argument cannot be null");
        Assert.notNull(dataSource, "[Assertion failed] - the dataSource argument cannot be null");
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        if (this.hasDataSource(dataSourceKey)) {
            logger.info("the same dataSource {} is exists", dataSourceKey);
            Assert.isTrue(this.autoCloseSameKeyDataSource, "the same dataSource ["+dataSourceKey+"] is exists");
            DS closingDataSource = this.targetDataSources.get(dataSourceKey);
            logger.info("the closing dataSource {}", closingDataSource);
            closingDataSource.close();
            this.targetDataSources.remove(dataSourceKey);
        }
        this.targetDataSources.put(dataSourceKey, dataSource);
        synchronized (refreshLock) {
            if (refresh) {
                this.afterPropertiesSet();
            }
        }
    }

    public void addDataSource(Object dataSourceKey, DS dataSource) throws SQLException {
        this.addDataSource(dataSourceKey, dataSource, true);
        logger.info("add dataSource finished.");
    }

    public void addDataSources(Map<Object, DS> dataSources) throws SQLException {
        if (ValidateKit.isNull(dataSources)) {
            return;
        }

        for(Map.Entry<Object, DS> entry: dataSources.entrySet()) {
            this.addDataSource(entry.getKey(), entry.getValue(), false);
        }
        synchronized (refreshLock) {
            this.afterPropertiesSet();
            logger.info("add dataSources finished.");
        }
    }

    public void removeDataSource(Object dataSourceKey) {
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        if (this.hasDataSource(dataSourceKey)) {
            DS deletingDataSource = this.targetDataSources.remove(dataSourceKey);
            if (ValidateKit.isNotNull(deletingDataSource)) {
                deletingDataSource.close();
            }
            synchronized (refreshLock) {
                this.afterPropertiesSet();
                logger.info("remove dataSource finished.");
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (ValidateKit.isNotNull(this.targetDataSources)) {
            for (Map.Entry<Object, DS> entry : this.targetDataSources.entrySet()) {
                entry.getValue().close();
                logger.info("dataSource {} closed.", entry.getKey());
            }
            this.targetDataSources.clear();
        }
    }

    /**
     * Specify whether to apply a lenient fallback to the default DataSource
     * if no specific DataSource could be found for the current lookup key.
     * <p>Default is "true", accepting lookup keys without a corresponding entry
     * in the target DataSource map - simply falling back to the default DataSource
     * in that case.
     * <p>Switch this flag to "false" if you would prefer the fallback to only apply
     * if the lookup key was {@code null}. Lookup keys without a DataSource
     * entry will then lead to an IllegalStateException.
     * @see #determineCurrentLookupKey()
     */
    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    /**
     * Set the DataSourceLookup implementation to use for resolving data source
     * name Strings in the targetDataSources map.
     * <p>Default is a {@link JndiDataSourceLookup}, allowing the JNDI names
     * of application server DataSources to be specified directly.
     */
    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }

    @Override
    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }
        this.resolvedDataSources = new HashMap<>(this.targetDataSources.size());
        this.targetDataSources.forEach((key, value) -> {
            Object lookupKey = resolveSpecifiedLookupKey(key);
            DataSource dataSource = resolveSpecifiedDataSource(value);
            this.resolvedDataSources.put(lookupKey, dataSource);
        });
        if (this.defaultTargetDataSource != null) {
            this.resolvedDefaultDataSource = resolveSpecifiedDataSource(this.defaultTargetDataSource);
        }
    }

    /**
     * Resolve the given lookup key object, as specified in the
     * targetDataSources map, into
     * the actual lookup key to be used for matching with the
     * {@link #determineCurrentLookupKey() current lookup key}.
     * <p>The default implementation simply returns the given key as-is.
     * @param lookupKey the lookup key object as specified by the user
     * @return the lookup key as needed for matching
     */
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    /**
     * Resolve the specified data source object into a DataSource instance.
     * <p>The default implementation handles DataSource instances and data source
     * names (to be resolved via a {@link #setDataSourceLookup DataSourceLookup}).
     * @param dataSource the data source value object as specified in the targetDataSources map
     * @return the resolved DataSource (never {@code null})
     * @throws IllegalArgumentException in case of an unsupported value type
     */
    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource) dataSource;
        }
        else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) dataSource);
        }
        else {
            throw new IllegalArgumentException(
                    "Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
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
     * a lookup in the targetDataSources map,
     * falls back to the specified
     * default target DataSource if necessary.
     * @see #determineCurrentLookupKey()
     */
    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        Object lookupKey = determineCurrentLookupKey();
        DataSource dataSource = this.resolvedDataSources.get(lookupKey);
        if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    /**
     * Determine the current lookup key. This will typically be
     * implemented to check a thread-bound transaction context.
     * <p>Allows for arbitrary keys. The returned key needs
     * to match the stored lookup key type, as resolved by the
     * {@link #resolveSpecifiedLookupKey} method.
     */
    @Nullable
    protected abstract Object determineCurrentLookupKey();
}
