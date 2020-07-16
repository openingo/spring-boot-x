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

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.datasource.holder.RoutingDataSourceHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractRoutingDataSourceX
 *
 * @author Qicz
 */
@Slf4j
public abstract class AbstractRoutingDataSourceX extends AbstractRoutingDataSource {
    
    @Nullable
    private ConcurrentHashMap<Object, Object> targetDataSources;

    private static final Object refreshLock = new Object();

    // auto close the same key dataSource
    private Boolean autoCloseSameKeyDataSource = true;

    private void addDataSource(Object dataSourceKey, DataSource dataSource, boolean refresh) {
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        Assert.notNull(dataSourceKey, "[Assertion failed] - the dataSourceKey argument cannot be null");
        Assert.notNull(dataSource, "[Assertion failed] - the dataSource argument cannot be null");
        if (this.hasDataSource(dataSourceKey)) {
            log.info("the same dataSource {} is exists", dataSourceKey);
            Assert.isTrue(this.autoCloseSameKeyDataSource, "the same dataSource ["+dataSourceKey+"] is exists");
            Object closingDataSource = this.targetDataSources.get(dataSourceKey);
            Boolean closeDataSourceState = this.closeDataSource(closingDataSource);
            if (ValidateKit.isTrue(closeDataSourceState)) {
                log.info("the dataSource with the key {} is closed", dataSourceKey);
                this.targetDataSources.remove(dataSourceKey);
            } else {
                log.error("the dataSource with the key {} has an error in close operations", dataSourceKey);
            }
        }
        this.targetDataSources.put(dataSourceKey, dataSource);
        synchronized (refreshLock) {
            if (refresh) {
                this.afterPropertiesSet();
            }
        }
    }

    public AbstractRoutingDataSourceX(DataSource defaultTargetDataSource) {
        this.targetDataSources = new ConcurrentHashMap<>();
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(this.targetDataSources);
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
     * @param dataSourceKey
     * @return <tt>true</tt> exists
     */
    public Boolean hasDataSource(Object dataSourceKey) {
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        return this.targetDataSources.containsKey(dataSourceKey);
    }

    /**
     * add a new dataSource
     * @param dataSourceKey
     * @param dataSource
     */
    public void addDataSource(Object dataSourceKey, DataSource dataSource) {
        this.addDataSource(dataSourceKey, dataSource, true);
        log.info("add dataSource finished.");
    }

    /**
     * add more dataSources
     * @param dataSources
     */
    public void addDataSources(Map<Object, DataSource> dataSources) {
        if (ValidateKit.isNull(dataSources)) {
            return;
        }

        for(Map.Entry<Object, DataSource> entry: dataSources.entrySet()) {
            this.addDataSource(entry.getKey(), entry.getValue(), false);
        }
        synchronized (refreshLock) {
            this.afterPropertiesSet();
            log.info("add dataSources finished.");
        }
    }

    /**
     * remove the dataSource with the {@code dataSourceKey}
     * @param dataSourceKey
     */
    public void removeDataSource(Object dataSourceKey) {
        Assert.notNull(this.targetDataSources, "[Assertion failed] - the targetDataSources argument cannot be null");
        if (this.hasDataSource(dataSourceKey)) {
            Object deletingDataSource = this.targetDataSources.remove(dataSourceKey);
            if (ValidateKit.isNotNull(deletingDataSource)) {
                this.closeDataSource(deletingDataSource);
            }
            synchronized (refreshLock) {
                this.afterPropertiesSet();
                log.info("remove dataSource finished.");
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (ValidateKit.isNotNull(this.targetDataSources)) {
            for (Map.Entry<Object, Object> entry : this.targetDataSources.entrySet()) {
                this.closeDataSource(entry.getValue());
                log.info("dataSource {} closed.", entry.getKey());
            }
            this.targetDataSources.clear();
        }
    }

    /**
     * Specify the map of target DataSources, with the lookup key as key.
     * The mapped value can either be a corresponding {@link DataSource}
     * instance or a data source name String (to be resolved via a
     * {@link #setDataSourceLookup DataSourceLookup}).
     * <p>The key can be of arbitrary type; this class implements the
     * generic lookup process only. The concrete key representation will
     * be handled by {@link #resolveSpecifiedLookupKey(Object)} and
     * {@link #determineCurrentLookupKey()}.
     *
     * @param targetDataSources
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = new ConcurrentHashMap<>(targetDataSources);
        super.setTargetDataSources(this.targetDataSources);
    }

    /**
     * Determine the current lookup key. This will typically be
     * implemented to check a thread-bound transaction context.
     * <p>Allows for arbitrary keys. The returned key needs
     * to match the stored lookup key type, as resolved by the
     * {@link #resolveSpecifiedLookupKey} method.
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceHolder.getCurrentUsingDataSourceKey();
    }

    /**
     * Close the dataSource
     * @param dataSourceInstance closing dataSource
     */
    public abstract Boolean closeDataSource(Object dataSourceInstance);
}
