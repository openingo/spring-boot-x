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

package org.openingo.spring.datasource.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.openingo.jdkits.lang.StrKit;

import javax.sql.DataSource;

/**
 * HikariDataSourceProvider
 * copy & reference from jfinal
 *
 * @author Qicz
 */
public class HikariDataSourceProvider implements IDataSourceProvider {

    /**
     * jdbc Url
     */
    private String jdbcUrl;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * default auto-commit behavior of connections returned from the pool
     * Default：true
     */
    private boolean autoCommit = true;

    /**
     * the maximum number of milliseconds that a client (that's you)
     * will wait for a connection from the pool
     * Default: 30000 (30 seconds)
     */
    private long connectionTimeout = 30000;

    /**
     * the maximum amount of time that a connection is allowed to sit idle in the pool
     * Default: 600000 (10 minutes)
     */
    private long idleTimeout = 600000;

    /**
     * the maximum lifetime of a connection in the pool
     * Default: 1800000 (30 minutes)
     */
    private long maxLifetime = 1800000;

    /**
     * If your driver supports JDBC4 we strongly recommend not setting this property.
     * This is for "legacy" databases that do not support the JDBC4 Connection.isValid() API
     * Default: none
     */
    private String connectionTestQuery = null;

    /**
     * the maximum size that the pool is allowed to reach, including both idle and in-use connections
     * Default: 10
     */
    private int maximumPoolSize = 10;

    /**
     * user-defined name for the connection pool and appears mainly in logging
     * Default: auto-generated
     */
    private String poolName = null;

    /**
     * This property controls whether Connections obtained from the pool are in read-only mode by default.
     * Default: false
     */
    private boolean readOnly = false;

    /**
     * the default catalog for databases that support the concept of catalogs.
     * Default: driver default
     */
    private String catalog = null;

    /**
     * a SQL statement that will be executed after every new connection creation before adding it to the pool
     * Default: none
     */
    private String connectionInitSql = null;

    /**
     * HikariCP will attempt to resolve a driver through the DriverManager based solely on the jdbcUrl,
     * but for some older drivers the driverClassName must also be specified
     * Default: none
     */
    private String driverClass = null;

    /**
     * the default transaction isolation level of connections returned from the pool.
     * Default: driver default
     */
    private String transactionIsolation = null;

    /**
     * the maximum amount of time that a connection will be tested for aliveness.
     * This value must be less than the connectionTimeout. Lowest acceptable validation timeout is 250 ms.
     * Default: 5000(5 seconds)
     */
    private long validationTimeout = 5000;

    /**
     * the amount of time that a connection can be out of the pool before a message is logged indicating a possible connection leak.
     * A value of 0 means leak detection is disabled. Lowest acceptable value for enabling leak detection is 2000 (2 seconds).
     * Default: 0
     */
    private long leakDetectionThreshold = 0;

    /**
     * Hikari DataSource
     */
    private HikariDataSource dataSource;

    public HikariDataSourceProvider(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HikariDataSourceProvider(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public HikariDataSourceProvider(String jdbcUrl, String username, String password, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public boolean build() {
        HikariConfig config = new HikariConfig();
        //设定基本参数
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        //设定额外参数
        config.setAutoCommit(autoCommit);
        config.setReadOnly(readOnly);

        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setValidationTimeout(validationTimeout);

        if (StrKit.notBlank(driverClass)){
            config.setDriverClassName(driverClass);
        }

        if (StrKit.notBlank(transactionIsolation)){
            config.setTransactionIsolation(transactionIsolation);
        }

        if (this.leakDetectionThreshold != 0){
            config.setLeakDetectionThreshold(leakDetectionThreshold);
        }

        if (StrKit.notBlank(catalog)){
            config.setCatalog(catalog);
        }

        if (StrKit.notBlank(connectionTestQuery)){
            config.setConnectionTestQuery(connectionTestQuery);
        }

        if (StrKit.notBlank(poolName)){
            config.setPoolName(poolName);
        }

        if (StrKit.notBlank(connectionInitSql)){
            config.setConnectionInitSql(connectionInitSql);
        }

        if (jdbcUrl.toLowerCase().contains(":mysql:")){
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "256");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }
        if (jdbcUrl.toLowerCase().contains(":postgresql:")){
            if (this.readOnly){
                config.addDataSourceProperty("readOnly", "true");
            }
            config.setConnectionTimeout(0);
            config.addDataSourceProperty("prepareThreshold", "3");
            config.addDataSourceProperty("preparedStatementCacheQueries", "128");
            config.addDataSourceProperty("preparedStatementCacheSizeMiB", "4");
        }

        dataSource = new HikariDataSource(config);
        return true;
    }

    @Override
    public boolean destroy() {
        if (dataSource != null)
            dataSource.close();
        return true;
    }

    public final void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public final void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public final void setConnectionTimeout(long connectionTimeoutMs) {
        this.connectionTimeout = connectionTimeoutMs;
    }

    public final void setIdleTimeout(long idleTimeoutMs) {
        this.idleTimeout = idleTimeoutMs;
    }

    public final void setMaxLifetime(long maxLifetimeMs) {
        this.maxLifetime = maxLifetimeMs;
    }

    public final void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public final void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public final void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
    }

    public final void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }

    public final void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public final void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public final void setTransactionIsolation(String isolationLevel) {
        this.transactionIsolation = isolationLevel;
    }

    public final void setValidationTimeout(long validationTimeoutMs) {
        this.validationTimeout = validationTimeoutMs;
    }

    public final void setLeakDetectionThreshold(long leakDetectionThresholdMs) {
        this.leakDetectionThreshold = leakDetectionThresholdMs;
    }
}
