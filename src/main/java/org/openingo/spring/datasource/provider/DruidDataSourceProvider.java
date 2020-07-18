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
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import org.openingo.jdkits.lang.StrKit;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DruidDataSourceProvider
 * copy & reference from jfinal
 *
 * @author Qicz
 */
public class DruidDataSourceProvider implements IDataSourceProvider {

    //连接池的名称
    protected String name = null;

    // 基本属性 url、user、password
    protected String url;
    protected String username;
    protected String password;
    protected String publicKey;
    protected String driverClass = null;	// 由 "com.mysql.jdbc.Driver" 改为 null 让 druid 自动探测 driverClass 值

    // 初始连接池大小、最小空闲连接数、最大活跃连接数
    protected int initialSize = 1;
    protected int minIdle = 10;
    protected int maxActive = 32;

    // 配置获取连接等待超时的时间
    protected long maxWait = DruidDataSource.DEFAULT_MAX_WAIT;

    // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    protected long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    // 配置连接在池中最小生存的时间
    protected long minEvictableIdleTimeMillis = DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    // 配置发生错误时多久重连
    protected long timeBetweenConnectErrorMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

    /**
     * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
     * Oracle - "select 1 from dual"
     * DB2 - "select 1 from sysibm.sysdummy1"
     * mysql - "select 1"
     */
    protected String validationQuery = "select 1";
    protected String  connectionInitSql = null;
    protected String connectionProperties = null;
    protected boolean testWhileIdle = true;
    protected boolean testOnBorrow = false;
    protected boolean testOnReturn = false;

    // 是否打开连接泄露自动检测
    protected boolean removeAbandoned = false;
    // 连接长时间没有使用，被认为发生泄露时长
    protected long removeAbandonedTimeoutMillis = 300 * 1000;
    // 发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错
    protected boolean logAbandoned = false;

    // 是否缓存preparedStatement，即PSCache，对支持游标的数据库性能提升巨大，如 oracle、mysql 5.5 及以上版本
    // protected boolean poolPreparedStatements = false;	// oracle、mysql 5.5 及以上版本建议为 true;

    // 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，使用oracle时可以设定此值。
    protected int maxPoolPreparedStatementPerConnectionSize = -1;

    protected Integer defaultTransactionIsolation = null;

    // 配置监控统计拦截的filters
    protected String filters;	// 监控统计："stat"    防SQL注入："wall"     组合使用： "stat,wall"
    protected List<Filter> filterList;

    protected DruidDataSource dataSource;
    protected volatile boolean isStarted = false;

    public DruidDataSourceProvider(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DruidDataSourceProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validationQuery = autoCheckValidationQuery(url);
    }

    public DruidDataSourceProvider(String url, String username, String password, String driverClass) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
        this.validationQuery = autoCheckValidationQuery(url);
    }

    public DruidDataSourceProvider(String url, String username, String password, String driverClass, String filters) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
        this.filters = filters;
        this.validationQuery = autoCheckValidationQuery(url);
    }

    private static String  autoCheckValidationQuery(String url){
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

    /**
     * connection sql in initial, eg set encoding or schema etc
     */
    public void setConnectionInitSql(String sql){
        this.connectionInitSql = sql;
    }

    public final String getName() {
        return name;
    }

    /**
     * 连接池名称
     *
     * @param name
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * 设置过滤器，如果要开启监控统计需要使用此方法或在构造方法中进行设置
     * <p>
     * 监控统计："stat"
     * 防SQL注入："wall"
     * 组合使用： "stat,wall"
     * </p>
     */
    public DruidDataSourceProvider setFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public synchronized DruidDataSourceProvider addFilter(Filter filter) {
        if (filterList == null)
            filterList = new ArrayList<Filter>();
        filterList.add(filter);
        return this;
    }

    /**
     * start the provider
     * @return true started
     */
    @Override
    public boolean startProviding() {
        if (isStarted)
            return true;

        dataSource = new DruidDataSource();
        if(this.name != null){
            dataSource.setName(this.name);
        }
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        if (driverClass != null)
            dataSource.setDriverClassName(driverClass);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        dataSource.setValidationQuery(validationQuery);
        if(StrKit.notBlank(connectionInitSql)){
            List<String> connectionInitSqls = new ArrayList<String>();
            connectionInitSqls.add(this.connectionInitSql);
            dataSource.setConnectionInitSqls(connectionInitSqls);
        }
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);

        dataSource.setRemoveAbandoned(removeAbandoned);
        dataSource.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
        dataSource.setLogAbandoned(logAbandoned);

        // maxPoolPreparedStatementPerConnectionSize>0, poolPreparedStatements when be set to true , see druid sources
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

        if (defaultTransactionIsolation != null) {
            dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
        }

        boolean hasSetConnectionProperties = false;
        if (StrKit.notBlank(filters)){
            try {
                dataSource.setFilters(filters);
                if(filters.contains("config")){
                    // check the public key
                    if(StrKit.isBlank(this.publicKey)){
                        throw new DruidRuntimeException("Druid setting the config filter, publicKey required.");
                    }
                    String decryptStr = "config.decrypt=true;config.decrypt.key="+this.publicKey;
                    String cp = this.connectionProperties;
                    if(StrKit.isBlank(cp)){
                        cp = decryptStr;
                    }else{
                        cp = cp + ";" + decryptStr;
                    }
                    dataSource.setConnectionProperties(cp);
                    hasSetConnectionProperties = true;
                }
            } catch (SQLException e) {throw new RuntimeException(e);}
        }
        // make sure the setConnectionProperties will be invoked once
        if(!hasSetConnectionProperties && StrKit.notBlank(this.connectionProperties)){
            dataSource.setConnectionProperties(this.connectionProperties);
        }
        addFilterList(dataSource);

        isStarted = true;
        return true;
    }

    private void addFilterList(DruidDataSource ds) {
        if (filterList != null) {
            List<Filter> targetList = ds.getProxyFilters();
            for (Filter add : filterList) {
                boolean found = false;
                for (Filter target : targetList) {
                    if (add.getClass().equals(target.getClass())) {
                        found = true;
                        break;
                    }
                }
                if (! found)
                    targetList.add(add);
            }
        }
    }

    /**
     * Destroy the provider
     * @return true destroyed
     */
    @Override
    public boolean destroy() {
        if (dataSource != null)
            dataSource.close();

        dataSource = null;
        isStarted = false;
        return true;
    }

    /**
     * Returns provider's dataSource
     * @return provider's dataSource
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    public DruidDataSourceProvider set(int initialSize, int minIdle, int maxActive) {
        this.initialSize = initialSize;
        this.minIdle = minIdle;
        this.maxActive = maxActive;
        return this;
    }

    public DruidDataSourceProvider setDriverClass(String driverClass) {
        this.driverClass = driverClass;
        return this;
    }

    public DruidDataSourceProvider setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    public DruidDataSourceProvider setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public DruidDataSourceProvider setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public DruidDataSourceProvider setMaxWait(long maxWait) {
        this.maxWait = maxWait;
        return this;
    }

    public DruidDataSourceProvider setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        return this;
    }

    public DruidDataSourceProvider setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        return this;
    }

    public DruidDataSourceProvider setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        return this;
    }

    /**
     * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
     * Oracle - "select 1 from dual"
     * DB2 - "select 1 from sysibm.sysdummy1"
     * mysql - "select 1"
     */
    public DruidDataSourceProvider setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
        return this;
    }

    public DruidDataSourceProvider setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        return this;
    }

    public DruidDataSourceProvider setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        return this;
    }

    public DruidDataSourceProvider setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        return this;
    }

    public DruidDataSourceProvider setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
        return this;
    }

    public final DruidDataSourceProvider setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
        return this;
    }

    public final DruidDataSourceProvider setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
        return this;
    }

    public final DruidDataSourceProvider setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
        return this;
    }

    public final DruidDataSourceProvider setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
        return this;
    }

    public final DruidDataSourceProvider setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
        return this;
    }

    public final DruidDataSourceProvider setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }
}
