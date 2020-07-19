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

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * HikariDataSourceProvider
 *
 * @author Qicz
 */
public class HikariDataSourceProvider extends HikariDataSource implements IDataSourceProvider {

    // provider Name
    private String providerName = null;

    private volatile boolean isStarted = false;

    public HikariDataSourceProvider(HikariDataSource dataSource) {
        super(dataSource);
    }

    public HikariDataSourceProvider(String jdbcUrl, String username, String password) {
        super.setJdbcUrl(jdbcUrl);
        super.setUsername(username);
        super.setPassword(password);
    }

    public HikariDataSourceProvider(String jdbcUrl, String username, String password, String driverClassName) {
        this(jdbcUrl, username, password);
        super.setDriverClassName(driverClassName);
    }

    public void setProviderName(String name) {
        this.providerName = name;
    }

    /**
     * Returns provider's dataSource
     * @return provider's dataSource
     */
    @Override
    public DataSource getDataSource() {
        return this;
    }

    @Override
    public final String getProviderName() {
        return providerName;
    }

    /**
     * start the provider
     * @return true started
     */
    @Override
    public boolean startProviding() {
        if (isStarted) {
            return true;
        }

        String jdbcUrl = super.getJdbcUrl().toLowerCase();

        if (jdbcUrl.contains(":mysql:")){
            super.addDataSourceProperty("cachePrepStmts", "true");
            super.addDataSourceProperty("useServerPrepStmts", "true");
            super.addDataSourceProperty("prepStmtCacheSize", "256");
            super.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }
        if (jdbcUrl.contains(":postgresql:")){
//            if (this.isReadOnly()){
//                super.addDataSourceProperty("readOnly", "true");
//            }
            super.setConnectionTimeout(0);
            super.addDataSourceProperty("prepareThreshold", "3");
            super.addDataSourceProperty("preparedStatementCacheQueries", "128");
            super.addDataSourceProperty("preparedStatementCacheSizeMiB", "4");
        }
        isStarted = true;
        return true;
    }

    /**
     * Destroy the provider
     * @return true destroyed
     */
    @Override
    public boolean destroy() {
        this.close();
        isStarted = false;
        return true;
    }
}
