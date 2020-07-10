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

import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;

/**
 * DefaultHyperLogLogOperationsX
 *
 * @author Qicz
 */
public class DefaultHyperLogLogOperationsX<V> extends DefaultHyperLogLogOperations<String, V> implements IKeyNamingPolicy {

    IKeyNamingPolicy keyNamingPolicy;

    public DefaultHyperLogLogOperationsX(RedisTemplate<String, V> template, IKeyNamingPolicy keyNamingPolicy) {
        super(template);
        this.keyNamingPolicy = keyNamingPolicy;
    }

    @Override
    public Long size(String... keys) {
        return super.size(this.getKeyNames(keys));
    }

    @Override
    public Long union(String destination, String... sourceKeys) {
        return super.union(this.getKeyName(destination), this.getKeyNames(sourceKeys));
    }

    @Override
    public void delete(String key) {
        super.delete(this.getKeyName(key));
    }

    @Override
    public String getKeyName(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }
}
