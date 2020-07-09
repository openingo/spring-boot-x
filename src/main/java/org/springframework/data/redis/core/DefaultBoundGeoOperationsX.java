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

package org.springframework.data.redis.core;

import org.openingo.jdkits.ClassKit;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * DefaultBoundGeoOperationsX
 *
 * @author Qicz
 */
public class DefaultBoundGeoOperationsX<V> extends DefaultBoundGeoOperations<String, V> {

    @Autowired
    IKeyNamingPolicy keyNamingPolicy;

    /**
     * Constructs a new {@code DefaultBoundGeoOperations}.
     *  @param key        must not be {@literal null}.
     * @param operations must not be {@literal null}.
     */
    public DefaultBoundGeoOperationsX(String key, RedisOperations<String, V> operations) {
        super(key, operations);
        this.rename(keyNamingPolicy.getKeyName(key));
    }

    /**
     * Renames the key. <br>
     * <b>Note:</b> The new name for empty collections will be propagated on add of first element.
     *
     * @param newKey new key. Must not be {@literal null}.
     */
    @Override
    public void rename(String newKey) {
        super.rename(keyNamingPolicy.getKeyName(newKey));
    }
}
