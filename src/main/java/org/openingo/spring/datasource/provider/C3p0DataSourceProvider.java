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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.lang.StrKit;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * C3p0DataSourceProvider
 * copy & reference from jfinal
 *
 * @author Qicz
 */
@Slf4j
public class C3p0DataSourceProvider implements IDataSourceProvider {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass = "com.mysql.jdbc.Driver";
    private int maxPoolSize = 100;
    private int minPoolSize = 10;
    private int initialPoolSize = 10;
    private int maxIdleTime = 20;
    private int acquireIncrement = 2;

    private ComboPooledDataSource dataSource;
    private volatile boolean isStarted = false;

    public C3p0DataSourceProvider(ComboPooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public C3p0DataSourceProvider setDriverClass(String driverClass) {
        if (StrKit.isBlank(driverClass))
            throw new IllegalArgumentException("driverClass can not be blank.");
        this.driverClass = driverClass;
        return this;
    }

    public C3p0DataSourceProvider setMaxPoolSize(int maxPoolSize) {
        if (maxPoolSize < 1)
            throw new IllegalArgumentException("maxPoolSize must more than 0.");
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public C3p0DataSourceProvider setMinPoolSize(int minPoolSize) {
        if (minPoolSize < 1)
            throw new IllegalArgumentException("minPoolSize must more than 0.");
        this.minPoolSize = minPoolSize;
        return this;
    }

    public C3p0DataSourceProvider setInitialPoolSize(int initialPoolSize) {
        if (initialPoolSize < 1)
            throw new IllegalArgumentException("initialPoolSize must more than 0.");
        this.initialPoolSize = initialPoolSize;
        return this;
    }

    public C3p0DataSourceProvider setMaxIdleTime(int maxIdleTime) {
        if (maxIdleTime < 1)
            throw new IllegalArgumentException("maxIdleTime must more than 0.");
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public C3p0DataSourceProvider setAcquireIncrement(int acquireIncrement) {
        if (acquireIncrement < 1)
            throw new IllegalArgumentException("acquireIncrement must more than 0.");
        this.acquireIncrement = acquireIncrement;
        return this;
    }

    public C3p0DataSourceProvider(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public C3p0DataSourceProvider(String jdbcUrl, String user, String password, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClass = driverClass != null ? driverClass : this.driverClass;
    }

    public C3p0DataSourceProvider(String jdbcUrl, String user, String password, String driverClass, Integer maxPoolSize, Integer minPoolSize, Integer initialPoolSize, Integer maxIdleTime, Integer acquireIncrement) {
        initC3p0Properties(jdbcUrl, user, password, driverClass, maxPoolSize, minPoolSize, initialPoolSize, maxIdleTime, acquireIncrement);
    }

    private void initC3p0Properties(String jdbcUrl, String user, String password, String driverClass, Integer maxPoolSize, Integer minPoolSize, Integer initialPoolSize, Integer maxIdleTime, Integer acquireIncrement) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClass = driverClass != null ? driverClass : this.driverClass;
        this.maxPoolSize = maxPoolSize != null ? maxPoolSize : this.maxPoolSize;
        this.minPoolSize = minPoolSize != null ? minPoolSize : this.minPoolSize;
        this.initialPoolSize = initialPoolSize != null ? initialPoolSize : this.initialPoolSize;
        this.maxIdleTime = maxIdleTime != null ? maxIdleTime : this.maxIdleTime;
        this.acquireIncrement = acquireIncrement != null ? acquireIncrement : this.acquireIncrement;
    }

    public C3p0DataSourceProvider(File propertyFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertyFile);
            Properties ps = new Properties();
            ps.load(fis);

            initC3p0Properties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"),
                    toInt(ps.getProperty("maxPoolSize")), toInt(ps.getProperty("minPoolSize")), toInt(ps.getProperty("initialPoolSize")),
                    toInt(ps.getProperty("maxIdleTime")),toInt(ps.getProperty("acquireIncrement")));
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
        }
        finally {
            if (fis != null)
                try {fis.close();} catch (IOException e) { log.error(e.getMessage(), e);}
        }
    }

    public C3p0DataSourceProvider(Properties properties) {
        Properties ps = properties;
        initC3p0Properties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"),
                toInt(ps.getProperty("maxPoolSize")), toInt(ps.getProperty("minPoolSize")), toInt(ps.getProperty("initialPoolSize")),
                toInt(ps.getProperty("maxIdleTime")),toInt(ps.getProperty("acquireIncrement")));
    }

    /**
     * start the provider
     * @return true started
     */
    @Override
    public boolean startProviding() {
        if (isStarted)
            return true;

        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        try {dataSource.setDriverClass(driverClass);}
        catch (PropertyVetoException e) {dataSource = null; System.err.println("C3p0Plugin start error"); throw new RuntimeException(e);}
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setInitialPoolSize(initialPoolSize);
        dataSource.setMaxIdleTime(maxIdleTime);
        dataSource.setAcquireIncrement(acquireIncrement);

        isStarted = true;
        return true;
    }

    private Integer toInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * Returns provider's dataSource
     * @return provider's dataSource
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    public ComboPooledDataSource getComboPooledDataSource() {
        return dataSource;
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
}
