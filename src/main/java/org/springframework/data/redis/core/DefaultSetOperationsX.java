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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * DefaultSetOperationsX
 *
 * @author Qicz
 */
public class DefaultSetOperationsX<V> extends DefaultSetOperations<String, V> {

    IKeyNamingPolicy keyNamingPolicy;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    private List<String> getKeys(Collection<String> keys) {
        return this.keyNamingPolicy.getKeyNames(keys);
    }

    public DefaultSetOperationsX<V> setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
        return this;
    }

    public DefaultSetOperationsX(RedisTemplate<String, V> template) {
        super(template);
    }

    @Override
    public Long add(String key, V... values) {
        return super.add(this.getKey(key), values);
    }

    @Override
    public Set<V> difference(String key, String otherKey) {
        return super.difference(this.getKey(key), this.getKey(otherKey));
    }

    @Override
    public Set<V> difference(String key, Collection<String> otherKeys) {
        return super.difference(this.getKey(key), this.getKeys(otherKeys));
    }

    @Override
    public Long differenceAndStore(String key, String otherKey, String destKey) {
        return super.differenceAndStore(this.getKey(key), this.getKey(otherKey), this.getKey(destKey));
    }

    @Override
    public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.differenceAndStore(this.getKey(key), this.getKeys(otherKeys), this.getKey(destKey));
    }

    @Override
    public Set<V> intersect(String key, String otherKey) {
        return super.intersect(this.getKey(key), this.getKey(otherKey));
    }

    @Override
    public Set<V> intersect(String key, Collection<String> otherKeys) {
        return super.intersect(this.getKey(key), this.getKeys(otherKeys));
    }

    @Override
    public Long intersectAndStore(String key, String otherKey, String destKey) {
        return super.intersectAndStore(this.getKey(key), this.getKey(otherKey), this.getKey(destKey));
    }

    @Override
    public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.intersectAndStore(this.getKey(key), this.getKeys(otherKeys), this.getKey(destKey));
    }

    @Override
    public Boolean isMember(String key, Object o) {
        return super.isMember(this.getKey(key), o);
    }

    @Override
    public Set<V> members(String key) {
        return super.members(this.getKey(key));
    }

    @Override
    public Boolean move(String key, V value, String destKey) {
        return super.move(this.getKey(key), value, this.getKey(destKey));
    }

    @Override
    public V randomMember(String key) {
        return super.randomMember(this.getKey(key));
    }

    @Override
    public Set<V> distinctRandomMembers(String key, long count) {
        return super.distinctRandomMembers(this.getKey(key), count);
    }

    @Override
    public List<V> randomMembers(String key, long count) {
        return super.randomMembers(this.getKey(key), count);
    }

    @Override
    public Long remove(String key, Object... values) {
        return super.remove(this.getKey(key), values);
    }

    @Override
    public V pop(String key) {
        return super.pop(this.getKey(key));
    }

    @Override
    public List<V> pop(String key, long count) {
        return super.pop(this.getKey(key), count);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKey(key));
    }

    @Override
    public Set<V> union(String key, String otherKey) {
        return super.union(this.getKey(key), this.getKey(otherKey));
    }

    @Override
    public Set<V> union(String key, Collection<String> otherKeys) {
        return super.union(this.getKey(key), this.getKeys(otherKeys));
    }

    @Override
    public Long unionAndStore(String key, String otherKey, String destKey) {
        return super.unionAndStore(this.getKey(key), this.getKey(otherKey), this.getKey(destKey));
    }

    @Override
    public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.unionAndStore(this.getKey(key), this.getKeys(otherKeys), this.getKey(destKey));
    }

    @Override
    public Cursor<V> scan(String key, ScanOptions options) {
        return super.scan(this.getKey(key), options);
    }
}
