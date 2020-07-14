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

package org.openingo.spring.extension.data.redis;

import org.springframework.data.redis.connection.DataType;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplateXLite
 *
 * The commonly used operations wrapper
 *
 * @author RedisTemplateXLite
 */
public class RedisTemplateXLite<V> extends RedisTemplateX<V> implements IRedisLiteOperations<V> {

    @Override
    public void set(String key, V value) {
        this.opsForValue().set(key, value);
    }

    @Override
    public void setex(String key, int seconds, V value) {
        this.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String key) {
        return (T)this.opsForValue().get(key);
    }

    @Override
    public Boolean del(String key) {
        return this.opsForValue().getOperations().delete(key);
    }

    @Override
    public Long del(List<String> keys) {
        return this.opsForValue().getOperations().delete(keys);
    }

    @Override
    public void mset(Object... keysValues) {

    }

    @Override
    public List<V> mget(List<String> keys) {
        return this.opsForValue().multiGet(keys);
    }

    @Override
    public Long decr(String key) {
        return this.opsForValue().decrement(key);
    }

    @Override
    public Long decrBy(String key, long value) {
        return this.opsForValue().decrement(key, value);
    }

    @Override
    public Long incr(String key) {
        return this.opsForValue().increment(key);
    }

    @Override
    public Long incrBy(String key, long value) {
        return this.opsForValue().increment(key, value);
    }

    @Override
    public Boolean exists(String key) {
        return this.opsForValue().getOperations().hasKey(key);
    }

    @Override
    public void rename(String oldKey, String newKey) {
        this.opsForValue().getOperations().rename(oldKey, newKey);
    }

    @Override
    public Boolean expire(String key, long seconds) {
        return this.opsForValue().getOperations().expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        return this.opsForValue().getOperations().expireAt(key, date);
    }

    @Override
    public Boolean pexpire(String key, long millSeconds) {
        return this.opsForValue().getOperations().expire(key, millSeconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> T getSet(String key, V value) {
        return (T)this.opsForValue().getAndSet(key, value);
    }

    @Override
    public Boolean persist(String key) {
        return this.opsForValue().getOperations().persist(key);
    }

    @Override
    public DataType type(String key) {
        return this.opsForValue().getOperations().type(key);
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long pttl(String key) {
        return null;
    }

    @Override
    public Long countExistingKeys(List<String> keys) {
        return this.opsForValue().getOperations().countExistingKeys(keys);
    }

    @Override
    public Long objectIdletime(String key) {
        return null;
    }

    @Override
    public void hset(String key, Object field, Object value) {
        this.opsForHash().put(key, field, value);
    }

    @Override
    public void hmset(String key, Map<Object, Object> hash) {
        this.opsForHash().putAll(key, hash);
    }

    @Override
    public <T> T hget(String key, Object field) {
        return (T)this.opsForHash().get(key, field);
    }

    @Override
    public <T> List<T> hmget(String key, List<Object> fields) {
        return new ArrayList(this.opsForHash().multiGet(key, fields));
    }

    @Override
    public Long hdel(String key, Object... fields) {
        return this.opsForHash().delete(key, fields);
    }

    @Override
    public Boolean hexists(String key, Object field) {
        return this.opsForHash().hasKey(key, field);
    }

    @Override
    public <HK, HV> Map<HK, HV> hgetAll(String key) {
        return (Map<HK, HV>)this.opsForHash().entries(key);
    }

    @Override
    public <HV> List<HV> hvals(String key) {
        return new ArrayList(this.opsForHash().values(key));
    }

    @Override
    public Set<String> hkeys(String key) {
        return new HashSet(this.opsForHash().keys(key));
    }

    @Override
    public Long hlen(String key) {
        return this.opsForHash().size(key);
    }

    @Override
    public <T> T lindex(String key, long index) {
        return (T)this.opsForList().index(key, index);
    }

    @Override
    public Long getCounter(String key) {
        return this.opsForList().size(key);
    }

    @Override
    public Long llen(String key) {
        return null;
    }

    @Override
    public <T> T lpop(String key) {
        return (T)this.opsForList().leftPop(key);
    }

    @Override
    public Long lpush(String key, V... values) {
        return this.opsForList().leftPushAll(key, values);
    }

    @Override
    public void lset(String key, long index, Object value) {

    }

    @Override
    public Long lrem(String key, long count, Object value) {
        return null;
    }

    @Override
    public <T> List<T> lrange(String key, long start, long end) {
        return null;
    }

    @Override
    public void ltrim(String key, long start, long end) {

    }

    @Override
    public <T> T rpop(String key) {
        return null;
    }

    @Override
    public <T> T rpoplpush(String srcKey, String dstKey) {
        return null;
    }

    @Override
    public Long rpush(String key, Object... values) {
        return null;
    }

    @Override
    public <T> List<T> blpop(String... keys) {
        return null;
    }

    @Override
    public <T> List<T> blpop(int timeout, String... keys) {
        return null;
    }

    @Override
    public <T> List<T> brpop(String... keys) {
        return null;
    }

    @Override
    public <T> List<T> brpop(int timeout, String... keys) {
        return null;
    }

    @Override
    public Long sadd(String key, Object... members) {
        return null;
    }

    @Override
    public Long scard(String key) {
        return null;
    }

    @Override
    public <T> T spop(String key) {
        return null;
    }

    @Override
    public <T> Set<T> smembers(String key) {
        return null;
    }

    @Override
    public Boolean sismember(String key, Object member) {
        return false;
    }

    @Override
    public <T> Set<T> sinter(String... keys) {
        return null;
    }

    @Override
    public <T> T srandmember(String key) {
        return null;
    }

    @Override
    public <T> List<T> srandmember(String key, int count) {
        return null;
    }

    @Override
    public Long srem(String key, Object... members) {
        return null;
    }

    @Override
    public <T> Set<T> sunion(String... keys) {
        return null;
    }

    @Override
    public <T> Set<T> sdiff(String... keys) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, Object member) {
        return null;
    }

    @Override
    public Long zadd(String key, Map<Object, Double> scoreMembers) {
        return null;
    }

    @Override
    public Long zcard(String key) {
        return null;
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return null;
    }

    @Override
    public Double zincrby(String key, double score, Object member) {
        return 0.0;
    }

    @Override
    public <T> Set<T> zrange(String key, long start, long end) {
        return null;
    }

    @Override
    public <T> Set<T> zrevrange(String key, long start, long end) {
        return null;
    }

    @Override
    public <T> Set<T> zrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zrank(String key, Object member) {
        return null;
    }

    @Override
    public Long zrevrank(String key, Object member) {
        return null;
    }

    @Override
    public Long zrem(String key, Object... members) {
        return null;
    }

    @Override
    public Double zscore(String key, Object member) {
        return 0.0;
    }


}
