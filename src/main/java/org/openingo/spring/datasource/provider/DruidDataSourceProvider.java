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

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.pool.DruidDataSource;
import org.openingo.jdkits.lang.StrKit;
import org.openingo.jdkits.validate.ValidateKit;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

/**
 * DruidDataSourceProvider
 *
 * validationQuery :
 * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
 * Oracle - "select 1 from dual"
 * DB2 - "select 1 from sysibm.sysdummy1"
 * mysql - "select 1"
 *
 * Filters:
 * statistics："stat"
 * Prevent SQL injection："wall"
 * combine： "stat,wall"
 *
 * @author Qicz
 */
public class DruidDataSourceProvider extends DruidDataSource implements IDataSourceProvider {

    // provider Name
    private String providerName = null;

    private String publicKey;

    private String filters;

    private volatile boolean isStarted = false;

    private DruidDataSource cloneInstance = null;

    private static String autoCheckValidationQuery(String url){
        if(url.startsWith("jdbc:oracle")){
            return "select 1 from dual";
        }else if(url.startsWith("jdbc:db2")){
            return "select 1 from sysibm.sysdummy1";
        }else if(url.startsWith("jdbc:hsqldb")){
            return "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        }else if(url.startsWith("jdbc:derby")){
            return "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
        }
        return "select 1";
    }

    private DruidDataSource deduceDataSource() {
        if (ValidateKit.isNotNull(this.cloneInstance)) {
            return this.cloneInstance;
        }
        return this;
    }

    private DruidDataSourceProvider() {
        super.setInitialSize(1);
        super.setMinIdle(10);
        super.setMaxActive(32);
        super.setMaxWait(DruidDataSource.DEFAULT_MAX_WAIT);
        super.setTimeBetweenEvictionRunsMillis(DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        super.setMinEvictableIdleTimeMillis(DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        super.setTimeBetweenConnectErrorMillis(DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS);
        super.setTestWhileIdle(true);
        super.setTestOnBorrow(false);
        super.setTestOnReturn(false);
        super.setRemoveAbandoned(false);
        super.setRemoveAbandonedTimeoutMillis(5 * 60 * 1000);
        super.setLogAbandoned(false);
        // oracle、mysql 5.5 and upper
        super.setPoolPreparedStatements(true);
        super.setMaxPoolPreparedStatementPerConnectionSize(-1);
    }

    public DruidDataSourceProvider(DruidDataSource dataSource) {
        this.cloneInstance = dataSource;
    }

    public DruidDataSourceProvider(String url, String username, String password) {
        this();
        super.setUrl(url);
        super.setUsername(username);
        super.setPassword(password);
        super.setValidationQuery(autoCheckValidationQuery(url));
    }

    public DruidDataSourceProvider(String url, String username, String password, String driverClass) {
        this(url, username, password);
        super.setDriverClassName(driverClass);
    }

    public DruidDataSourceProvider(String url, String username, String password, String driverClass, String filters) {
        this(url, username, password, driverClass);
        this.setFilters(filters);
    }

    public void setProviderName(String name) {
        this.providerName = name;
    }

    public void set(int initialSize, int minIdle, int maxActive) {
        DruidDataSource dataSource = this.deduceDataSource();
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setConnectionInitSqls(String... sqls) {
        this.deduceDataSource().setConnectionInitSqls(Arrays.asList(sqls));
    }

    public void setFilters(String filters)  {
        this.filters = filters;
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

        if (StrKit.notBlank(this.filters)) {
            Properties connectProperties = null;
            if(this.filters.contains("config")) {
                // check the public key
                if(StrKit.isBlank(this.publicKey)){
                    throw new DruidRuntimeException("Druid setting the config filter, publicKey required.");
                }
                connectProperties = this.getConnectProperties();
                if (ValidateKit.isNull(connectProperties)) {
                    connectProperties = new Properties();
                }
                connectProperties.setProperty("config.decrypt", "true");
                connectProperties.setProperty("config.decrypt.key", this.publicKey);
            }
            try {
                // can not use the deduce dataSource, need call super logic when set filters
                if (ValidateKit.isNotNull(this.cloneInstance)) {
                    this.cloneInstance.setFilters(this.filters);
                    this.cloneInstance.setConnectProperties(connectProperties);
                } else {
                    super.setFilters(this.filters);
                    super.setConnectProperties(connectProperties);
                }
            } catch (SQLException e) {throw new DruidRuntimeException(e);}
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
        this.deduceDataSource().close();
        isStarted = false;
        return true;
    }

    /**
     * Returns provider's dataSource
     * @return provider's dataSource
     */
    @Override
    public DataSource getDataSource() {
        return this.deduceDataSource();
    }

    @Override
    public String getProviderName() {
        return this.providerName;
    }

    @Override
    public DruidDataSource cloneDruidDataSource() {
        if (ValidateKit.isNotNull(this.cloneInstance)) {
            return this.cloneInstance.cloneDruidDataSource();
        }
        return super.cloneDruidDataSource();
    }
}
