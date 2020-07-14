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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IRedisLiteOperations
 *
 * @author Qicz
 */
public interface IRedisLiteOperations<V> {

    void set(String key, V value);
    void setex(String key, int seconds, V value);
    <T> T get(String key);
    Boolean del(String key);
    Long del(List<String> keys);

    void mset(Object... keysValues);
    List<V> mget(List<String> keys);

    Long decr(String key);
    Long decrBy(String key, long value);
    Long incr(String key);
    Long incrBy(String key, long value);
    Boolean exists(String key);
    void rename(String oldKey, String newKey);

    Boolean expire(String key, long seconds);
    Boolean expireAt(String key, Date date);
    Boolean pexpire(String key, long millSeconds);

    <T> T getSet(String key, V value);
    Boolean persist(String key);
    DataType type(String key);
    Long ttl(String key);
    Long pttl(String key);
    Long countExistingKeys(List<String> keys);
    Long objectIdletime(String key);

    void hset(String key, Object field, Object value);
    void hmset(String key, Map<Object, Object> hash);
    <T> T hget(String key, Object field);
    <T> List<T> hmget(String key, List<Object> fields);
    Long hdel(String key, Object... fields);
    Boolean hexists(String key, Object field);
    <HK, HV> Map<HK, HV> hgetAll(String key);
    <HV> List<HV> hvals(String key);
    Set<String> hkeys(String key);
    Long hlen(String key);

    <T> T lindex(String key, long index);
    Long getCounter(String key);
    Long llen(String key);
    <T> T lpop(String key);
    Long lpush(String key, V... values);
    void lset(String key, long index, Object value);
    Long lrem(String key, long count, Object value);
    <T> List<T> lrange(String key, long start, long end);
    void ltrim(String key, long start, long end);

    <T> T rpop(String key);
    <T> T rpoplpush(String srcKey, String dstKey);
    Long rpush(String key, Object... values);
    <T> List<T> blpop(String... keys);
    <T> List<T> blpop(int timeout, String... keys);
    <T> List<T> brpop(String... keys);
    <T> List<T> brpop(int timeout, String... keys);

    Long sadd(String key, Object... members);
    Long scard(String key);
    <T> T spop(String key);
    <T> Set<T> smembers(String key);
    Boolean sismember(String key, Object member);
    <T> Set<T> sinter(String... keys);
    <T> T srandmember(String key);
    <T> List<T> srandmember(String key, int count);
    Long srem(String key, Object... members);
    <T> Set<T> sunion(String... keys);
    <T> Set<T> sdiff(String... keys);
    Long zadd(String key, double score, Object member);
    Long zadd(String key, Map<Object, Double> scoreMembers);
    Long zcard(String key);
    Long zcount(String key, double min, double max);
    Double zincrby(String key, double score, Object member);
    <T> Set<T> zrange(String key, long start, long end);
    <T> Set<T> zrevrange(String key, long start, long end);
    <T> Set<T> zrangeByScore(String key, double min, double max);
    Long zrank(String key, Object member);
    Long zrevrank(String key, Object member);
    Long zrem(String key, Object... members);
    Double zscore(String key, Object member);
}
