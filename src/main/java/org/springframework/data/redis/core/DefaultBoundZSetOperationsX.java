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

import com.sun.istack.internal.NotNull;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * DefaultBoundZSetOperationsX
 *
 * @author Qicz
 */
public class DefaultBoundZSetOperationsX<V> extends DefaultBoundZSetOperations<String, V> implements IBoundHashOperationsX {

    IKeyNamingPolicy keyNamingPolicy;

    String originKey;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    public DefaultBoundZSetOperationsX<V> setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
        return this;
    }

    /**
     * Constructs a new <code>DefaultBoundZSetOperations</code> instance.
     *
     * @param key
     * @param operations
     */
    public DefaultBoundZSetOperationsX(String key, RedisOperations<String, V> operations) {
        super(key, operations);
        this.originKey = key;
        this.rename(key);
    }

    /**
     * Get origin Key
     *
     * @return origin key
     */
    @Override
    public String getOriginKey() {
        return this.originKey;
    }

    @Override
    public void rename(String newKey) {
        super.rename(this.getKey(newKey));
    }
}
