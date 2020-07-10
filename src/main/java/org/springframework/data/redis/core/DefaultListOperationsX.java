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
public class DefaultListOperationsX<V> extends DefaultListOperations<String, V> implements IKeyNamingPolicy {

    IKeyNamingPolicy keyNamingPolicy;

    public DefaultListOperationsX(RedisTemplate<String, V> template, IKeyNamingPolicy keyNamingPolicy) {
        super(template);
        this.keyNamingPolicy = keyNamingPolicy;
    }

    @Override
    public V index(String key, long index) {
        return super.index(this.getKeyName(key), index);
    }

    @Override
    public V leftPop(String key) {
        return super.leftPop(this.getKeyName(key));
    }

    @Override
    public V leftPop(String key, long timeout, TimeUnit unit) {
        return super.leftPop(this.getKeyName(key), timeout, unit);
    }

    @Override
    public Long leftPush(String key, V value) {
        return super.leftPush(this.getKeyName(key), value);
    }

    @Override
    public Long leftPushAll(String key, V... values) {
        return super.leftPushAll(this.getKeyName(key), values);
    }

    @Override
    public Long leftPushAll(String key, Collection<V> values) {
        return super.leftPushAll(this.getKeyName(key), values);
    }

    @Override
    public Long leftPushIfPresent(String key, V value) {
        return super.leftPushIfPresent(this.getKeyName(key), value);
    }

    @Override
    public Long leftPush(String key, V pivot, V value) {
        return super.leftPush(this.getKeyName(key), pivot, value);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKeyName(key));
    }

    @Override
    public List<V> range(String key, long start, long end) {
        return super.range(this.getKeyName(key), start, end);
    }

    @Override
    public Long remove(String key, long count, Object value) {
        return super.remove(this.getKeyName(key), count, value);
    }

    @Override
    public V rightPop(String key) {
        return super.rightPop(this.getKeyName(key));
    }

    @Override
    public V rightPop(String key, long timeout, TimeUnit unit) {
        return super.rightPop(this.getKeyName(key), timeout, unit);
    }

    @Override
    public Long rightPush(String key, V value) {
        return super.rightPush(this.getKeyName(key), value);
    }

    @Override
    public Long rightPushAll(String key, V... values) {
        return super.rightPushAll(this.getKeyName(key), values);
    }

    @Override
    public Long rightPushAll(String key, Collection<V> values) {
        return super.rightPushAll(this.getKeyName(key), values);
    }

    @Override
    public Long rightPushIfPresent(String key, V value) {
        return super.rightPushIfPresent(this.getKeyName(key), value);
    }

    @Override
    public Long rightPush(String key, V pivot, V value) {
        return super.rightPush(this.getKeyName(key), pivot, value);
    }

    @Override
    public V rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return super.rightPopAndLeftPush(this.getKeyName(sourceKey), this.getKeyName(destinationKey));
    }

    @Override
    public V rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return super.rightPopAndLeftPush(this.getKeyName(sourceKey), this.getKeyName(destinationKey), timeout, unit);
    }

    @Override
    public void set(String key, long index, V value) {
        super.set(this.getKeyName(key), index, value);
    }

    @Override
    public void trim(String key, long start, long end) {
        super.trim(this.getKeyName(key), start, end);
    }

    @Override
    public String getKeyName(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }
}
