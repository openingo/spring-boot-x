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

package org.openingo.spring.boot.extension.datasource.holder;

import lombok.extern.slf4j.Slf4j;
import org.openingo.java.lang.ThreadLocalX;
import org.openingo.jdkits.validate.ValidateKit;

/**
 * RoutingDataSourceHolder
 *
 * @author Qicz
 */
@Slf4j
public final class RoutingDataSourceHolder {

    private RoutingDataSourceHolder(){}

    private static final ThreadLocalX<Object> ROUTING_DATASOURCE_HOLDER = new ThreadLocalX<>();

    /**
     * Set current using dataSource Key
     * @param dataSourceKey current using dataSource Key
     */
    public static void setCurrentUsingDataSourceKey(Object dataSourceKey) {
        log.info("The key for current setting dataSource is \"{}\"", dataSourceKey);
        ROUTING_DATASOURCE_HOLDER.set(dataSourceKey);
    }

    /**
     * Returns the current using dataSource key
     * @return current using dataSource Key
     */
    public static Object getCurrentUsingDataSourceKey() {
        Object usingDataSourceKey = ROUTING_DATASOURCE_HOLDER.get();
        String extMessage = ValidateKit.isNull(usingDataSourceKey) ? ", that will use the default dataSource" : "";
        log.info("The key for current using dataSource is \"{}\"{}.", usingDataSourceKey, extMessage);
        return usingDataSourceKey;
    }

    /**
     * Manual remove current using dataSource Key
     */
    public static void clearCurrentUsingDataSourceKey() {
        Object removingDataSourceKey = ROUTING_DATASOURCE_HOLDER.get();
        log.info("The key for current removing dataSource is \"{}\"", removingDataSourceKey);
        ROUTING_DATASOURCE_HOLDER.remove();
    }
}
