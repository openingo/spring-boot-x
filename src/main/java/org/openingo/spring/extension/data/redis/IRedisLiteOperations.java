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
    long del(String key);
    long del(String... keys);

    void mset(Object... keysValues);
    List<V> mget(String... keys);

    long decr(String... key);
    long decrBy(String key, long value);
    long incr(String key);
    long incrBy(String key, long value);
    boolean exists(String key);
    String randomKey();
    void rename(String oldKey, String newKey);

    long expire(String key, int seconds);
    long expireAt(String key, long unixTime);
    long pexpire(String key, long millSeconds);
    long pexpireAt(String key, long milliSecondsTimestamp);

    <T> T getSet(String key, V value);
    long persist(String key);
    String type(String key);
    long ttl(String key);
    long pttl(String key);
    long objectRefcount(String key);
    long objectIdletime(String key);

    long hset(String key, Object field, Object value);
    void hmset(String key, Map<Object, Object> hash);
    <T> T hget(String key, Object field);
    <T> List<T> hmget(String key, Object... fields);
    long hdel(String key, Object... fields);
    boolean hexists(String key, Object field);
    <HK, HV> Map<HK, HV> hgetAll(String key);
    <HV> List<HV> hvals(String key);
    Set<String> hkeys(String key);
    long hlen(String key);

    <T> T lindex(String key, long index);
    long getCounter(String key);
    long llen(String key);
    <T> T lpop(String key);
    long lpush(String key, Object... values);
    void lset(String key, long index, Object value);
    long lrem(String key, long count, Object value);
    <T> List<T> lrange(String key, long start, long end);
    void ltrim(String key, long start, long end);

    <T> T rpop(String key);
    <T> T rpoplpush(String srcKey, String dstKey);
    long rpush(String key, Object... values);
    <T> List<T> blpop(String... keys);
    <T> List<T> blpop(int timeout, String... keys);
    <T> List<T> brpop(String... keys);
    <T> List<T> brpop(int timeout, String... keys);

    long sadd(String key, Object... members);
    long scard(String key);
    <T> T spop(String key);
    <T> Set<T> smembers(String key);
    boolean sismember(String key, Object member);
    <T> Set<T> sinter(String... keys);
    <T> T srandmember(String key);
    <T> List<T> srandmember(String key, int count);
    long srem(String key, Object... members);
    <T> Set<T> sunion(String... keys);
    <T> Set<T> sdiff(String... keys);
    long zadd(String key, double score, Object member);
    long zadd(String key, Map<Object, Double> scoreMembers);
    long zcard(String key);
    long zcount(String key, double min, double max);
    double zincrby(String key, double score, Object member);
    <T> Set<T> zrange(String key, long start, long end);
    <T> Set<T> zrevrange(String key, long start, long end);
    <T> Set<T> zrangeByScore(String key, double min, double max);
    long zrank(String key, Object member);
    long zrevrank(String key, Object member);
    long zrem(String key, Object... members);
    double zscore(String key, Object member);
}
