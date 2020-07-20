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

package org.openingo.x.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.openingo.spring.datasource.holder.RoutingDataSourceHolder;
import org.openingo.spring.datasource.provider.DruidDataSourceProvider;
import org.openingo.spring.datasource.routing.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * DataSourceService
 *
 * @author Qicz
 */
@Service
@Slf4j
public class DataSourceService implements IDataSourceService {

    @Autowired
    RoutingDataSource routingDataSource;

    @Autowired
    DruidDataSource dataSource;

    @Override
    public void switchDataSource(String name) throws SQLException {
        try {
            System.out.println("======before======"+name);
            routingDataSource.getConnection();
            System.out.println(routingDataSource.getCurrentUsingDataSourceProvider().hashCode());
            RoutingDataSourceHolder.setCurrentUsingDataSourceKey(name);
            routingDataSource.getConnection();
            System.out.println("======after======");
            System.out.println(routingDataSource.getCurrentUsingDataSourceProvider().hashCode());
        } finally {
            RoutingDataSourceHolder.clearCurrentUsingDataSourceKey();
        }
    }

    @Override
    public void add(String name) {
        //routingDataSource.setAutoCloseSameKeyDataSource(false);
        DruidDataSourceProvider druidDataSourceProvider = new DruidDataSourceProvider(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        druidDataSourceProvider.startProviding();
        routingDataSource.addDataSource(name, druidDataSourceProvider);
    }
}
