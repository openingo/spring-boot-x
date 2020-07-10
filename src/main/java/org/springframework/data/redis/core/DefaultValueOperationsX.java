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
import org.springframework.data.redis.connection.BitFieldSubCommands;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DefaultValueOperationsX
 *
 * @author Qicz
 */
public class DefaultValueOperationsX<V> extends DefaultValueOperations<String, V> implements IKeyNamingPolicy{

    IKeyNamingPolicy keyNamingPolicy;

    public DefaultValueOperationsX(RedisTemplate<String, V> template,
                                   IKeyNamingPolicy keyNamingPolicy) {
        super(template);
        this.keyNamingPolicy = keyNamingPolicy;
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return super.get(this.getKeyName(key.toString()));
        }
        return super.get(key);
    }

    @Override
    public V getAndSet(String key, V newValue) {
        return super.getAndSet(this.getKeyName(key), newValue);
    }

    @Override
    public Long increment(String key) {
        return super.increment(this.getKeyName(key));
    }

    @Override
    public Long increment(String key, long delta) {
        return super.increment(this.getKeyName(key), delta);
    }

    @Override
    public Double increment(String key, double delta) {
        return super.increment(this.getKeyName(key), delta);
    }

    @Override
    public Long decrement(String key) {
        return super.decrement(this.getKeyName(key));
    }

    @Override
    public Long decrement(String key, long delta) {
        return super.decrement(this.getKeyName(key), delta);
    }

    @Override
    public Integer append(String key, String value) {
        return super.append(this.getKeyName(key), value);
    }

    @Override
    public String get(String key, long start, long end) {
        return super.get(this.getKeyName(key), start, end);
    }

    @Override
    public List<V> multiGet(Collection<String> keys) {
        return super.multiGet(this.getKeyNames(keys));
    }

    @Override
    public void multiSet(Map<? extends String, ? extends V> m) {
        super.multiSet(this.mapKeyNaming(m));
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends String, ? extends V> m) {
        return super.multiSetIfAbsent(this.mapKeyNaming(m));
    }

    @Override
    public void set(String key, V value) {
        super.set(this.getKeyName(key), value);
    }

    @Override
    public void set(String key, V value, long timeout, TimeUnit unit) {
        super.set(this.getKeyName(key), value, timeout, unit);
    }

    @Override
    public Boolean setIfAbsent(String key, V value) {
        return super.setIfAbsent(this.getKeyName(key), value);
    }

    @Override
    public Boolean setIfAbsent(String key, V value, long timeout, TimeUnit unit) {
        return super.setIfAbsent(this.getKeyName(key), value, timeout, unit);
    }

    @Override
    public Boolean setIfPresent(String key, V value) {
        return super.setIfPresent(this.getKeyName(key), value);
    }

    @Override
    public Boolean setIfPresent(String key, V value, long timeout, TimeUnit unit) {
        return super.setIfPresent(this.getKeyName(key), value, timeout, unit);
    }

    @Override
    public void set(String key, V value, long offset) {
        super.set(this.getKeyName(key), value, offset);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKeyName(key));
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        return super.setBit(this.getKeyName(key), offset, value);
    }

    @Override
    public Boolean getBit(String key, long offset) {
        return super.getBit(this.getKeyName(key), offset);
    }

    @Override
    public List<Long> bitField(String key, BitFieldSubCommands subCommands) {
        return super.bitField(this.getKeyName(key), subCommands);
    }

    @Override
    public String getKeyName(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }
}
