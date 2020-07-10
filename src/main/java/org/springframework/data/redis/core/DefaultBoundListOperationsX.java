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

import org.openingo.spring.extension.data.redis.RedisTemplateX;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;

import java.util.Collection;
import java.util.List;

/**
 * DefaultBoundListOperationsX
 *
 * @author Qicz
 */
public class DefaultBoundListOperationsX<V> extends DefaultBoundListOperations<String, V> implements IBoundOperationsX, IKeyNamingPolicy {

    IKeyNamingPolicy keyNamingPolicy;

    String originKey;

    /**
     * Constructs a new <code>DefaultBoundListOperations</code> instance.
     *
     * @param key
     * @param operations
     * @param keyNamingPolicy
     */
    public DefaultBoundListOperationsX(String key, RedisOperations<String, V> operations, IKeyNamingPolicy keyNamingPolicy) {
        super(key, operations);
        this.keyNamingPolicy = keyNamingPolicy;
        this.originKey = key;
        this.rename(key);
    }

    @Override
    public void rename(String newKey) {
        super.rename(this.getKeyName(newKey));
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
    public String getKeyName(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }
}
