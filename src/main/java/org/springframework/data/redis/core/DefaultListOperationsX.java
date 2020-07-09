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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DefaultListOperationsX
 *
 * @author Qicz
 */
public class DefaultListOperationsX<V> extends DefaultListOperations<String, V> {

    IKeyNamingPolicy keyNamingPolicy;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    public DefaultListOperationsX<V> setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
        return this;
    }

    public DefaultListOperationsX(RedisTemplate<String, V> template) {
        super(template);
    }

    @Override
    public V index(String key, long index) {
        return super.index(this.getKey(key), index);
    }

    @Override
    public V leftPop(String key) {
        return super.leftPop(this.getKey(key));
    }

    @Override
    public V leftPop(String key, long timeout, TimeUnit unit) {
        return super.leftPop(this.getKey(key), timeout, unit);
    }

    @Override
    public Long leftPush(String key, V value) {
        return super.leftPush(this.getKey(key), value);
    }

    @Override
    public Long leftPushAll(String key, V... values) {
        return super.leftPushAll(this.getKey(key), values);
    }

    @Override
    public Long leftPushAll(String key, Collection<V> values) {
        return super.leftPushAll(this.getKey(key), values);
    }

    @Override
    public Long leftPushIfPresent(String key, V value) {
        return super.leftPushIfPresent(this.getKey(key), value);
    }

    @Override
    public Long leftPush(String key, V pivot, V value) {
        return super.leftPush(this.getKey(key), pivot, value);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKey(key));
    }

    @Override
    public List<V> range(String key, long start, long end) {
        return super.range(this.getKey(key), start, end);
    }

    @Override
    public Long remove(String key, long count, Object value) {
        return super.remove(this.getKey(key), count, value);
    }

    @Override
    public V rightPop(String key) {
        return super.rightPop(this.getKey(key));
    }

    @Override
    public V rightPop(String key, long timeout, TimeUnit unit) {
        return super.rightPop(this.getKey(key), timeout, unit);
    }

    @Override
    public Long rightPush(String key, V value) {
        return super.rightPush(this.getKey(key), value);
    }

    @Override
    public Long rightPushAll(String key, V... values) {
        return super.rightPushAll(this.getKey(key), values);
    }

    @Override
    public Long rightPushAll(String key, Collection<V> values) {
        return super.rightPushAll(this.getKey(key), values);
    }

    @Override
    public Long rightPushIfPresent(String key, V value) {
        return super.rightPushIfPresent(this.getKey(key), value);
    }

    @Override
    public Long rightPush(String key, V pivot, V value) {
        return super.rightPush(this.getKey(key), pivot, value);
    }

    @Override
    public V rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return super.rightPopAndLeftPush(this.getKey(sourceKey), this.getKey(destinationKey));
    }

    @Override
    public V rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return super.rightPopAndLeftPush(this.getKey(sourceKey), this.getKey(destinationKey), timeout, unit);
    }

    @Override
    public void set(String key, long index, V value) {
        super.set(this.getKey(key), index, value);
    }

    @Override
    public void trim(String key, long start, long end) {
        super.trim(this.getKey(key), start, end);
    }
}
