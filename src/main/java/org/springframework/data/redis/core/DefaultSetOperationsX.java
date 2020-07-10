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
import java.util.Set;

/**
 * DefaultSetOperationsX
 *
 * @author Qicz
 */
public class DefaultSetOperationsX<V> extends DefaultSetOperations<String, V> implements IKeyNamingPolicy {

    IKeyNamingPolicy keyNamingPolicy;

    public DefaultSetOperationsX(RedisTemplate<String, V> template, IKeyNamingPolicy keyNamingPolicy) {
        super(template);
        this.keyNamingPolicy = keyNamingPolicy;
    }

    @Override
    public Long add(String key, V... values) {
        return super.add(this.getKeyName(key), values);
    }

    @Override
    public Set<V> difference(String key, String otherKey) {
        return super.difference(this.getKeyName(key), this.getKeyName(otherKey));
    }

    @Override
    public Set<V> difference(String key, Collection<String> otherKeys) {
        return super.difference(this.getKeyName(key), this.getKeyNames(otherKeys));
    }

    @Override
    public Long differenceAndStore(String key, String otherKey, String destKey) {
        return super.differenceAndStore(this.getKeyName(key), this.getKeyName(otherKey), this.getKeyName(destKey));
    }

    @Override
    public Long differenceAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.differenceAndStore(this.getKeyName(key), this.getKeyNames(otherKeys), this.getKeyName(destKey));
    }

    @Override
    public Set<V> intersect(String key, String otherKey) {
        return super.intersect(this.getKeyName(key), this.getKeyName(otherKey));
    }

    @Override
    public Set<V> intersect(String key, Collection<String> otherKeys) {
        return super.intersect(this.getKeyName(key), this.getKeyNames(otherKeys));
    }

    @Override
    public Long intersectAndStore(String key, String otherKey, String destKey) {
        return super.intersectAndStore(this.getKeyName(key), this.getKeyName(otherKey), this.getKeyName(destKey));
    }

    @Override
    public Long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.intersectAndStore(this.getKeyName(key), this.getKeyNames(otherKeys), this.getKeyName(destKey));
    }

    @Override
    public Boolean isMember(String key, Object o) {
        return super.isMember(this.getKeyName(key), o);
    }

    @Override
    public Set<V> members(String key) {
        return super.members(this.getKeyName(key));
    }

    @Override
    public Boolean move(String key, V value, String destKey) {
        return super.move(this.getKeyName(key), value, this.getKeyName(destKey));
    }

    @Override
    public V randomMember(String key) {
        return super.randomMember(this.getKeyName(key));
    }

    @Override
    public Set<V> distinctRandomMembers(String key, long count) {
        return super.distinctRandomMembers(this.getKeyName(key), count);
    }

    @Override
    public List<V> randomMembers(String key, long count) {
        return super.randomMembers(this.getKeyName(key), count);
    }

    @Override
    public Long remove(String key, Object... values) {
        return super.remove(this.getKeyName(key), values);
    }

    @Override
    public V pop(String key) {
        return super.pop(this.getKeyName(key));
    }

    @Override
    public List<V> pop(String key, long count) {
        return super.pop(this.getKeyName(key), count);
    }

    @Override
    public Long size(String key) {
        return super.size(this.getKeyName(key));
    }

    @Override
    public Set<V> union(String key, String otherKey) {
        return super.union(this.getKeyName(key), this.getKeyName(otherKey));
    }

    @Override
    public Set<V> union(String key, Collection<String> otherKeys) {
        return super.union(this.getKeyName(key), this.getKeyNames(otherKeys));
    }

    @Override
    public Long unionAndStore(String key, String otherKey, String destKey) {
        return super.unionAndStore(this.getKeyName(key), this.getKeyName(otherKey), this.getKeyName(destKey));
    }

    @Override
    public Long unionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return super.unionAndStore(this.getKeyName(key), this.getKeyNames(otherKeys), this.getKeyName(destKey));
    }

    @Override
    public Cursor<V> scan(String key, ScanOptions options) {
        return super.scan(this.getKeyName(key), options);
    }

    @Override
    public String getKeyName(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }
}
