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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DefaultHashOperationsX
 *
 * @author Qicz
 */
public class DefaultHashOperationsX<HK, HV> extends DefaultHashOperations<String, HK, HV> {

    IKeyNamingPolicy keyNamingPolicy;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    public DefaultHashOperationsX<HK, HV> setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
        return this;
    }

    public DefaultHashOperationsX(RedisTemplate<String, ?> template) {
        super(template);
    }

    @Override
    public Set<HK> keys(String key) {
        return super.keys(this.getKey(key));
    }

    @Override
    public HV get(String key, Object hashKey) {
        return super.get(this.getKey(key), hashKey);
    }

    @Override
    public Boolean hasKey(String key, Object hashKey) {
        return super.hasKey(this.getKey(key), hashKey);
    }

    @Override
    public Long increment(String key, HK hashKey, long delta) {
        return super.increment(this.getKey(key), hashKey, delta);
    }

    @Override
    public Double increment(String key, HK hashKey, double delta) {
        return super.increment(this.getKey(key), hashKey, delta);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKey(key));
    }

    @Override
    public Long lengthOfValue(String key, HK hashKey) {
        return super.lengthOfValue(this.getKey(key), hashKey);
    }

    @Override
    public void putAll(String key, Map<? extends HK, ? extends HV> m) {
        super.putAll(this.getKey(key), m);
    }

    @Override
    public List<HV> multiGet(String key, Collection<HK> fields) {
        return super.multiGet(this.getKey(key), fields);
    }

    @Override
    public void put(String key, HK hashKey, HV value) {
        super.put(this.getKey(key), hashKey, value);
    }

    @Override
    public Boolean putIfAbsent(String key, HK hashKey, HV value) {
        return super.putIfAbsent(this.getKey(key), hashKey, value);
    }

    @Override
    public List<HV> values(String key) {
        return super.values(this.getKey(key));
    }

    @Override
    public Long delete(String key, Object... hashKeys) {
        return super.delete(this.getKey(key), hashKeys);
    }

    @Override
    public Map<HK, HV> entries(String key) {
        return super.entries(this.getKey(key));
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(String key, ScanOptions options) {
        return super.scan(this.getKey(key), options);
    }
}
