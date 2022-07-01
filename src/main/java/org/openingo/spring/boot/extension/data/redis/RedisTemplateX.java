/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
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

package org.openingo.spring.boot.extension.data.redis;

import org.openingo.jdkits.collection.ListKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.boot.extension.data.redis.callback.SessionCallbackX;
import org.openingo.spring.boot.extension.data.redis.commands.IRedisCommands;
import org.openingo.spring.boot.extension.data.redis.commands.IValueCommands;
import org.openingo.spring.boot.extension.data.redis.core.DefaultSessionCallback;
import org.openingo.spring.boot.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplateX
 *
 * @author Qicz
 */
@SuppressWarnings("all")
public class RedisTemplateX<K, V> implements IRedisCommands<K, V> {

    private RedisTemplate<K, V> redisTemplate;

    @Autowired
    private IKeyNamingPolicy keyNamingPolicy;

    private K namingKey(K key) {
        if (Objects.nonNull(this.keyNamingPolicy) && key instanceof String) {
            return (K)this.keyNamingPolicy.getKeyName(key.toString());
        }
        return key;
    }

    private Collection<K> namingKeys(Collection<K> keys) {
        if (Objects.isNull(this.keyNamingPolicy)) {
            return keys;
        }
        List<K> converted = new ArrayList<>();
        keys.forEach(key -> {
            if (key instanceof String) {
                key = (K)this.keyNamingPolicy.getKeyName(key.toString());
            }
            converted.add(key);
        });
        return converted;
    }

    private K[] namingKeys(K... keys) {
        List<K> converted = new ArrayList<>();
        if (Objects.nonNull(keys) && keys.length != 0) {
            for (K key : keys) {
                if (key instanceof String) {
                    key = (K)this.keyNamingPolicy.getKeyName(key.toString());
                }
                converted.add(key);
            }
        }
        return (K[])converted.toArray();
    }

    public RedisTemplateX(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ClusterOperations<K, V> opsForCluster() {
        return this.redisTemplate.opsForCluster();
    }

    public GeoOperations<K, V> opsForGeo() {
        return this.redisTemplate.opsForGeo();
    }

    public BoundGeoOperations<K, V> boundGeoOps(K key) {
        return this.redisTemplate.boundGeoOps(key);
    }

    public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        return this.redisTemplate.boundHashOps(key);
    }

    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return this.redisTemplate.opsForHash();
    }

    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return this.redisTemplate.opsForHyperLogLog();
    }

    public ListOperations<K, V> opsForList() {
        return this.redisTemplate.opsForList();
    }

    public BoundListOperations<K, V> boundListOps(K key) {
        return this.redisTemplate.boundListOps(key);
    }

    public BoundSetOperations<K, V> boundSetOps(K key) {
        return this.redisTemplate.boundSetOps(key);
    }

    public SetOperations<K, V> opsForSet() {
        return this.redisTemplate.opsForSet();
    }

    public BoundValueOperations<K, V> boundValueOps(K key) {
        return this.redisTemplate.boundValueOps(key);
    }

    public ValueOperations<K, V> opsForValue() {
        return this.redisTemplate.opsForValue();
    }

    public BoundZSetOperations<K, V> boundZSetOps(K key) {
        return this.redisTemplate.boundZSetOps(key);
    }

    public ZSetOperations<K, V> opsForZSet() {
        return this.redisTemplate.opsForZSet();
    }

    public RedisTemplate<K, V> getRedisTemplate() {
        return this.redisTemplate;
    }

    public void setEnableTransactionSupport(Boolean enableTransactionSupport) {
        this.redisTemplate.setEnableTransactionSupport(enableTransactionSupport);
    }

    /**
     * Set {@code value} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    @Override
    public void set(K key, V value) {
        this.opsForValue().set(this.namingKey(key), value);
    }

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param timeoutSeconds the key expiration timeout.
     * @see <a href="https://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    @Override
    public void setEx(K key, long timeoutSeconds, V value) {
        this.opsForValue().set(this.namingKey(key), value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key     must not be {@literal null}.
     * @param timeout the key expiration timeout.
     * @param value   must not be {@literal null}.
     * @param unit    must not be {@literal null}.
     * @see <a href="https://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    @Override
    public void setEx(K key, long timeout, V value, TimeUnit unit) {
        this.opsForValue().set(this.namingKey(key), value, timeout, unit);
    }

    /**
     * Set {@code key} to hold the string {@code value} if {@code key} is absent.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     */
    @Override
    public Boolean setNx(K key, V value) {
        return this.opsForValue().setIfAbsent(this.namingKey(key), value);
    }

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is absent.
     *
     * @param key            must not be {@literal null}.
     * @param value          must not be {@literal null}.
     * @param timeoutSeconds the key expiration timeout.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     * @since 2.1
     */
    @Override
    public Boolean setNx(K key, V value, long timeoutSeconds) {
        return this.opsForValue().setIfAbsent(this.namingKey(key), value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is absent.
     *
     * @param key     must not be {@literal null}.
     * @param value   must not be {@literal null}.
     * @param timeout the key expiration timeout.
     * @param unit    must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     * @since 2.1
     */
    @Override
    public Boolean setNx(K key, V value, long timeout, TimeUnit unit) {
        return this.opsForValue().setIfAbsent(this.namingKey(key), value, timeout, unit);
    }

    /**
     * Set {@code key} to hold the string {@code value} if {@code key} is present.
     *
     * redis: SET key value XX
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return command result indicating if the key has been set.
     * @throws IllegalArgumentException if either {@code key} or {@code value} is not present.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    @Override
    public Boolean setXx(K key, V value) {
        return this.opsForValue().setIfPresent(this.namingKey(key), value);
    }

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is present.
     *
     * redis: SET key value EX seconds XX
     *
     * @param key            must not be {@literal null}.
     * @param value          must not be {@literal null}.
     * @param timeoutSeconds the key expiration timeout.
     * @return command result indicating if the key has been set.
     * @throws IllegalArgumentException if either {@code key}, {@code value} or {@code timeout} is not present.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    @Override
    public Boolean setXx(K key, V value, long timeoutSeconds) {
        return this.opsForValue().setIfPresent(this.namingKey(key), value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is present.
     *
     * redis: SET key value [EX seconds|PX milliseconds] XX
     *
     * @param key     must not be {@literal null}.
     * @param value   must not be {@literal null}.
     * @param timeout the key expiration timeout.
     * @param unit    must not be {@literal null}.
     * @return command result indicating if the key has been set.
     * @throws IllegalArgumentException if either {@code key}, {@code value} or {@code timeout} is not present.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    @Override
    public Boolean setXx(K key, V value, long timeout, TimeUnit unit) {
        return this.opsForValue().setIfPresent(this.namingKey(key), value, timeout, unit);
    }

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    @Override
    public V get(K key) {
        return this.opsForValue().get(this.namingKey(key));
    }

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Override
    public Boolean del(K key) {
        return this.opsForValue().getOperations().delete(this.namingKey(key));
    }

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Override
    public Long del(Collection<K> keys) {
        return this.opsForValue().getOperations().delete(this.namingKeys(keys));
    }

    /**
     * Set multiple keys to multiple values using key-value pairs.
     *
     * "key1", "value1", "key2", "values"
     *
     * @param keysValues must not be {@literal null}.
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    @Override
    public void mSet(Object... keysValues) {
        int length = keysValues.length;
        if (length % 2 != 0) {
            throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
        }
        List<K> keys = ListKit.emptyArrayList(length/2);
        List<V> values = ListKit.emptyArrayList(length/2);
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                keys.add((K)keysValues[i]);
            } else {
                values.add((V)keysValues[i]);
            }
        }
        Map<K, V> keysValuesMap = new HashMap<>(length);
        for (int i = 0; i < keys.size(); i++) {
            keysValuesMap.put(this.namingKey(keys.get(i)), values.get(i));
        }
        this.opsForValue().multiSet(keysValuesMap);
    }

    /**
     * Set multiple keys to multiple values using key-value pairs provided in {@code tuple}.
     *
     * @param map must not be {@literal null}.
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    @Override
    public void mSet(Map<K, V> map) {
        Map<K, V> convertedMap = new HashMap<>();
        map.keySet().forEach(key -> {
            convertedMap.put(this.namingKey(key), map.get(key));
        });
        this.opsForValue().multiSet(convertedMap);
    }

    /**
     * Get multiple {@code keys}. Values are returned in the order of the requested keys.
     *
     * @param keys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    @Override
    public List<V> mGet(Collection<K> keys) {
        return this.opsForValue().multiGet(this.namingKeys(keys));
    }

    /**
     * Decrement an integer value stored as string value under {@code key} by one.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/decr">Redis Documentation: DECR</a>
     */
    @Override
    public Long decr(K key) {
        return this.opsForValue().decrement(this.namingKey(key));
    }

    /**
     * Decrement an integer value stored as string value under {@code key} by {@code delta}.
     *
     * @param key must not be {@literal null}.
     * @param delta
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/decrby">Redis Documentation: DECRBY</a>
     */
    @Override
    public Long decrBy(K key, long delta) {
        return this.opsForValue().decrement(this.namingKey(key), delta);
    }

    /**
     * Increment an integer value stored as string value under {@code key} by one.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/incr">Redis Documentation: INCR</a>
     */
    @Override
    public Long incr(K key) {
        return this.opsForValue().increment(this.namingKey(key));
    }

    /**
     * Increment an integer value stored as string value under {@code key} by {@code delta}.
     *
     * @param key must not be {@literal null}.
     * @param delta
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     */
    @Override
    public Long incrBy(K key, long delta) {
        return this.opsForValue().increment(this.namingKey(key), delta);
    }

    /**
     * Determine if given {@code key} exists.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Override
    public Boolean exists(K key) {
        return this.opsForValue().getOperations().hasKey(this.namingKey(key));
    }

    /**
     * Rename key {@code oldKey} to {@code newKey}.
     *
     * @param oldKey must not be {@literal null}.
     * @param newKey must not be {@literal null}.
     * @see <a href="https://redis.io/commands/rename">Redis Documentation: RENAME</a>
     */
    @Override
    public void rename(K oldKey, K newKey) {
        this.opsForValue().getOperations().rename(this.namingKey(oldKey), this.namingKey(newKey));
    }

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key must not be {@literal null}.
     * @param timeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean expire(K key, long timeoutSeconds) {
        return this.opsForValue().getOperations().expire(this.namingKey(key), timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Set the expiration for given {@code key} as a {@literal date} timestamp.
     *
     * @param key must not be {@literal null}.
     * @param date must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean expireAt(K key, Date date) {
        return this.opsForValue().getOperations().expireAt(this.namingKey(key), date);
    }

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key must not be {@literal null}.
     * @param millTimeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean pExpire(K key, long millTimeoutSeconds) {
        return this.opsForValue().getOperations().expire(this.namingKey(key), millTimeoutSeconds, TimeUnit.MILLISECONDS);
    }

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getset">Redis Documentation: GETSET</a>
     */
    @Override
    public V getSet(K key, V value) {
        return this.opsForValue().getAndSet(this.namingKey(key), value);
    }

    /**
     * Remove the expiration from given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     */
    @Override
    public Boolean persist(K key) {
        return this.opsForValue().getOperations().persist(this.namingKey(key));
    }

    /**
     * Determine the type stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/type">Redis Documentation: TYPE</a>
     */
    @Override
    public DataType type(K key) {
        return this.opsForValue().getOperations().type(this.namingKey(key));
    }

    /**
     * Get the time to live for {@code key} in seconds.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/ttl">Redis Documentation: TTL</a>
     */
    @Override
    public Long ttl(K key) {
        return this.opsForValue().getOperations().getExpire(this.namingKey(key));
    }

    /**
     * Count the number of {@code keys} that exist.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys existing among the ones specified as arguments. Keys mentioned multiple times and
     *         existing are counted multiple times.
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Override
    public Long countExistingKeys(Collection<K> keys) {
        return this.opsForValue().getOperations().countExistingKeys(this.namingKeys(keys));
    }

    /**
     * Append a {@code value} to {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/append">Redis Documentation: APPEND</a>
     */
    @Override
    public Integer append(K key, String value) {
        return this.opsForValue().append(this.namingKey(key), value);
    }

    /**
     * Get a substring of value of {@code key} between {@code begin} and {@code end}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     */
    @Override
    public String getRange(K key, long start, long end) {
        return this.opsForValue().get(this.namingKey(key), start, end);
    }

    /**
     * Overwrite parts of {@code key} starting at the specified {@code offset} with given {@code value}.
     *
     * @param key    must not be {@literal null}.
     * @param value
     * @param offset
     * @see <a href="https://redis.io/commands/setrange">Redis Documentation: SETRANGE</a>
     */
    @Override
    public void setRange(K key, V value, long offset) {
        this.opsForValue().set(this.namingKey(key), value, offset);
    }

    /**
     * Get the length of the value stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     */
    @Override
    public Long strLen(K key) {
        return this.opsForValue().size(this.namingKey(key));
    }

    /**
     * Sets the bit at {@code offset} in value stored at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param offset
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/setbit">Redis Documentation: SETBIT</a>
     */
    @Override
    public Boolean setBit(K key, long offset, boolean value) {
        return this.opsForValue().setBit(this.namingKey(key), offset, value);
    }

    /**
     * Get the bit value at {@code offset} of value at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param offset
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getbit">Redis Documentation: GETBIT</a>
     */
    @Override
    public Boolean getBit(K key, long offset) {
        return this.opsForValue().getBit(this.namingKey(key), offset);
    }

    /**
     * Get / Manipulate specific integer fields of varying bit widths and arbitrary non (necessary) aligned offset stored
     * at a given {@code key}.
     *
     * @param key         must not be {@literal null}.
     * @param subCommands must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/bitfield">Redis Documentation: BITFIELD</a>
     * @since 2.1
     */
    @Override
    public List<Long> bitField(K key, BitFieldSubCommands subCommands) {
        return this.opsForValue().bitField(this.namingKey(key), subCommands);
    }

    /**
     * Set the {@code value} of a hash {@code hashKey}.
     *
     * @param key must not be {@literal null}.
     * @param hashKey must not be {@literal null}.
     * @param value
     * @see <a href="https://redis.io/commands/hset">Redis Documentation: HSET</a>
     */
    @Override
    public void hSet(K key, Object hashKey, Object value) {
        this.opsForHash().put(this.namingKey(key), hashKey, value);
    }

    /**
     * Set multiple hash fields to multiple values using data provided in {@code m}.
     *
     * @param key must not be {@literal null}.
     * @param m must not be {@literal null}.
     * @see <a href="https://redis.io/commands/hmset">Redis Documentation: HMSET</a>
     */
    @Override
    public void hmset(K key, Map<Object, Object> m) {
        this.opsForHash().putAll(this.namingKey(key), m);
    }

    /**
     * Get value for given {@code hashKey} from hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param hashKey must not be {@literal null}.
     * @return {@literal null} when key or hashKey does not exist or used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Override
    public <T> T hGet(K key, Object hashKey) {
        return (T)this.opsForHash().get(this.namingKey(key), hashKey);
    }

    /**
     * Get values for given {@code hashKeys} from hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param hashKeys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hmget">Redis Documentation: HMGET</a>
     */
    @Override
    public <T> List<T> hMget(K key, Collection<Object> hashKeys) {
        return new ArrayList(this.opsForHash().multiGet(this.namingKey(key), hashKeys));
    }

    /**
     * Delete given hash {@code hashKeys}.
     *
     * @param key must not be {@literal null}.
     * @param hashKeys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hdel">Redis Documentation: HDEL</a>
     */
    @Override
    public Long hDel(K key, Object... hashKeys) {
        return this.opsForHash().delete(this.namingKey(key), hashKeys);
    }

    /**
     * Determine if given hash {@code hashKey} exists.
     *
     * @param key must not be {@literal null}.
     * @param hashKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hexists">Redis Documentation: HEXISTS</a>
     */
    @Override
    public Boolean hExists(K key, Object hashKey) {
        return this.opsForHash().hasKey(this.namingKey(key), hashKey);
    }

    /**
     * Get entire hash stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hgetall">Redis Documentation: HGETALL</a>
     */
    @Override
    public <HK, HV> Map<HK, HV> hGetAll(K key) {
        return new HashMap(this.opsForHash().entries(this.namingKey(key)));
    }

    /**
     * Get entry set (values) of hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hvals">Redis Documentation: HVALS</a>
     */
    @Override
    public <HV> List<HV> hVals(K key) {
        return new ArrayList(this.opsForHash().values(this.namingKey(key)));
    }

    /**
     * Get key set (fields) of hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hkeys">Redis Documentation: HKEYS</a>
     */
    @Override
    public <HK> Set<HK> hKeys(K key) {
        return new HashSet(this.opsForHash().keys(this.namingKey(key)));
    }

    /**
     * Get size of hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/hlen">Redis Documentation: HLEN</a>
     */
    @Override
    public Long hLen(K key) {
        return this.opsForHash().size(this.namingKey(key));
    }

    /**
     * Get element at {@code index} form list at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param index
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
     */
    @Override
    public V lIndex(K key, long index) {
        return this.opsForList().index(this.namingKey(key), index);
    }

    /**
     * Get the size of list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/llen">Redis Documentation: LLEN</a>
     */
    @Override
    public Long lLen(K key) {
        return this.opsForList().size(this.namingKey(key));
    }

    /**
     * Removes and returns first element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     */
    @Override
    public V lPop(K key) {
        return this.opsForList().leftPop(this.namingKey(key));
    }

    /**
     * Prepend {@code value} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    @Override
    public Long lPush(K key, V value) {
        return this.opsForList().leftPush(this.namingKey(key), value);
    }

    /**
     * Prepend {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    @Override
    public Long lPush(K key, V... values) {
        return this.opsForList().leftPushAll(this.namingKey(key), values);
    }

    /**
     * Prepend {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @since 1.5
     * @see <a href="https://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    @Override
    public Long lPush(K key, Collection<V> values) {
        return this.opsForList().leftPushAll(this.namingKey(key), values);
    }

    /**
     * Prepend {@code values} to {@code key} only if the list exists.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
     */
    @Override
    public Long lPushx(K key, V value) {
        return this.opsForList().leftPushIfPresent(this.namingKey(key), value);
    }

    /**
     * Set the {@code value} list element at {@code index}.
     *
     * @param key must not be {@literal null}.
     * @param index
     * @param value
     * @see <a href="https://redis.io/commands/lset">Redis Documentation: LSET</a>
     */
    @Override
    public void lSet(K key, long index, V value) {
        this.opsForList().set(this.namingKey(key), index, value);
    }

    /**
     * Removes the first {@code count} occurrences of {@code value} from the list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param count
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lrem">Redis Documentation: LREM</a>
     */
    @Override
    public Long lRem(K key, long count, V value) {
        return this.opsForList().remove(this.namingKey(key), count, value);
    }

    /**
     * Get elements between {@code begin} and {@code end} from list at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
     */
    @Override
    public List<V> lRange(K key, long start, long end) {
        return this.opsForList().range(this.namingKey(key), start, end);
    }

    /**
     * Trim list at {@code key} to elements between {@code start} and {@code end}.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @see <a href="https://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     */
    @Override
    public void ltrim(K key, long start, long end) {
        this.opsForList().trim(this.namingKey(key), start, end);
    }

    /**
     * Removes and returns last element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/rpop">Redis Documentation: RPOP</a>
     */
    @Override
    public V rPop(K key) {
        return this.opsForList().rightPop(this.namingKey(key));
    }

    /**
     * Remove the last element from list at {@code sourceKey}, append it to {@code destinationKey} and return its value.
     *
     * @param srcKey must not be {@literal null}.
     * @param dstKey must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
     */
    @Override
    public V rPoplPush(K srcKey, K dstKey) {
        return this.opsForList().rightPopAndLeftPush(this.namingKey(srcKey), this.namingKey(dstKey));
    }

    /**
     * Remove the last element from list at {@code srcKey}, append it to {@code dstKey} and return its value.<br>
     * <b>Blocks connection</b> until element available or {@code timeoutSeconds} reached.
     *
     * @param srcKey must not be {@literal null}.
     * @param dstKey must not be {@literal null}.
     * @param timeoutSeconds
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/brpoplpush">Redis Documentation: BRPOPLPUSH</a>
     */
    @Override
    public V brPoplPush(K srcKey, K dstKey, long timeoutSeconds) {
        return this.opsForList().rightPopAndLeftPush(this.namingKey(srcKey), this.namingKey(dstKey), timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Append {@code value} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    @Override
    public Long rPush(K key, V value) {
        return this.opsForList().rightPush(this.namingKey(key), value);
    }

    /**
     * Append {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    @Override
    public Long rPush(K key, V... values) {
        return this.opsForList().rightPushAll(this.namingKey(key), values);
    }

    /**
     * Append {@code values} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    @Override
    public Long rPush(K key, Collection<V> values) {
        return this.opsForList().rightPushAll(this.namingKey(key), values);
    }

    /**
     * Append {@code values} to {@code key} only if the list exists.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
     */
    @Override
    public Long rPushx(K key, V value) {
        return this.opsForList().rightPushIfPresent(this.namingKey(key), value);
    }

    /**
     * Removes and returns first element from lists stored at {@code key} . <br>
     * <b>Blocks connection</b> until element available or {@code timeoutSeconds} reached.
     *
     * @param key must not be {@literal null}.
     * @param timeoutSeconds
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     */
    @Override
    public V blPop(K key, long timeoutSeconds) {
        return this.opsForList().leftPop(this.namingKey(key), timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Removes and returns last element from lists stored at {@code key}. <br>
     * <b>Blocks connection</b> until element available or {@code timeoutSeconds} reached.
     *
     * @param key must not be {@literal null}.
     * @param timeoutSeconds
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/brpop">Redis Documentation: BRPOP</a>
     */
    @Override
    public V brPop(K key, long timeoutSeconds) {
        return this.opsForList().rightPop(this.namingKey(key), timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Add given {@code values} to set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param values
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sadd">Redis Documentation: SADD</a>
     */
    @Override
    public Long sAdd(K key, V... values) {
        return this.opsForSet().add(this.namingKey(key), values);
    }

    /**
     * Get size of set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/scard">Redis Documentation: SCARD</a>
     */
    @Override
    public Long sCard(K key) {
        return this.opsForSet().size(this.namingKey(key));
    }

    /**
     * Remove and return a random member from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/spop">Redis Documentation: SPOP</a>
     */
    @Override
    public V sPop(K key) {
        return this.opsForSet().pop(this.namingKey(key));
    }

    /**
     * Get all elements of set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
     */
    @Override
    public Set<V> sMembers(K key) {
        return this.opsForSet().members(this.namingKey(key));
    }

    /**
     * Check if set at {@code key} contains {@code value}.
     *
     * @param key must not be {@literal null}.
     * @param member
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
     */
    @Override
    public Boolean sIsMember(K key, Object member) {
        return this.opsForSet().isMember(this.namingKey(key), member);
    }

    /**
     * Returns the members intersecting all given sets at {@code key} and {@code otherKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    @Override
    public Set<V> sInter(K key, K otherKey) {
        return this.opsForSet().intersect(this.namingKey(key), this.namingKey(otherKey));
    }

    /**
     * Returns the members intersecting all given sets at {@code key} and {@code otherKeys}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    @Override
    public Set<V> sInter(K key, Collection<K> otherKeys) {
        return this.opsForSet().intersect(this.namingKey(key), this.namingKeys(otherKeys));
    }

    /**
     * Intersect all given sets at {@code key} and {@code otherKey} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    @Override
    public Long sInterstore(K key, K otherKey, K destKey) {
        return this.opsForSet().intersectAndStore(this.namingKey(key), this.namingKey(otherKey), this.namingKey(destKey));
    }

    /**
     * Intersect all given sets at {@code key} and {@code otherKeys} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    @Override
    public Long sInterStore(K key, Collection<K> otherKeys, K destKey) {
        return this.opsForSet().intersectAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey));
    }

    /**
     * Get random element from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    @Override
    public V sRandMember(K key) {
        return this.opsForSet().randomMember(this.namingKey(key));
    }

    /**
     * Get {@code count} random elements from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param count nr of members to return.
     * @return empty {@link List} if {@code key} does not exist or {@literal null} when used in pipeline / transaction.
     * @throws IllegalArgumentException if count is negative.
     * @see <a href="https://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    @Override
    public List<V> sRandMember(K key, long count) {
        return this.opsForSet().randomMembers(this.namingKey(key), count);
    }

    /**
     * Remove given {@code values} from set at {@code key} and return the number of removed elements.
     *
     * @param key must not be {@literal null}.
     * @param members
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/srem">Redis Documentation: SREM</a>
     */
    @Override
    public Long sRem(K key, Object... members) {
        return this.opsForSet().remove(this.namingKey(key), members);
    }

    /**
     * Union all sets at given {@code keys} and {@code otherKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    @Override
    public Set<V> sUnion(K key, K otherKey) {
        return this.opsForSet().union(this.namingKey(key), this.namingKey(otherKey));
    }

    /**
     * Union all sets at given {@code keys} and {@code otherKeys}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    @Override
    public Set<V> sUnion(K key, Collection<K> otherKeys) {
        return this.opsForSet().union(this.namingKey(key), this.namingKeys(otherKeys));
    }

    /**
     * Union all sets at given {@code key} and {@code otherKey} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     */
    @Override
    public Long sUnionStore(K key, K otherKey, K destKey) {
        return this.opsForSet().unionAndStore(this.namingKey(key), this.namingKey(otherKey), this.namingKey(destKey));
    }

    /**
     * Union all sets at given {@code key} and {@code otherKeys} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     */
    @Override
    public Long sUnionStore(K key, Collection<K> otherKeys, K destKey) {
        return this.opsForSet().unionAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey));
    }

    /**
     * Diff all sets for given {@code key} and {@code otherKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    @Override
    public Set<V> sDiff(K key, K otherKey) {
        return this.opsForSet().difference(this.namingKey(key), this.namingKey(otherKey));
    }

    /**
     * Diff all sets for given {@code key} and {@code otherKeys}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    @Override
    public Set<V> sDiff(K key, Collection<K> otherKeys) {
        return this.opsForSet().difference(this.namingKey(key), this.namingKeys(otherKeys));
    }

    /**
     * Diff all sets for given {@code key} and {@code otherKey} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
     */
    @Override
    public Long sDiffStore(K key, K otherKey, K destKey) {
        return this.opsForSet().differenceAndStore(this.namingKey(key), this.namingKey(otherKey), this.namingKey(destKey));
    }

    /**
     * Diff all sets for given {@code key} and {@code otherKeys} and store result in {@code destKey}.
     *
     * @param key must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
     */
    @Override
    public Long sDiffStore(K key, Collection<K> otherKeys, K destKey) {
        return this.opsForSet().differenceAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey));
    }

    /**
     * Add {@code member} to a sorted set at {@code key}, or update its {@code score} if it already exists.
     *
     * @param key must not be {@literal null}.
     * @param member the member.
     * @param score the score.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     */
    @Override
    public Boolean zAdd(K key, V member, double score) {
        return this.opsForZSet().add(this.namingKey(key), member, score);
    }

    /**
     * Add {@code scoreMembers} to a sorted set at {@code key}, or update its {@code score} if it already exists.
     *
     * @param key must not be {@literal null}.
     * @param scoreMembers must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     */
    @Override
    public Long zAdd(K key, Map<V, Double> scoreMembers) {
        if (ValidateKit.isNull(scoreMembers)) {
            return 0L;
        }

        Set<ZSetOperations.TypedTuple<V>> tuples = new HashSet<>();
        scoreMembers.forEach((k, score) -> tuples.add(new DefaultTypedTuple<>(k, score)));

        return this.opsForZSet().add(this.namingKey(key), tuples);
    }

    /**
     * Returns the number of elements of the sorted set stored with given {@code key}.
     *
     * @param key
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zcard">Redis Documentation: ZCARD</a>
     */
    @Override
    public Long zCard(K key) {
        return this.opsForZSet().size(this.namingKey(key));
    }

    /**
     * Count number of elements within sorted set with scores between {@code min} and {@code max}.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zcount">Redis Documentation: ZCOUNT</a>
     */
    @Override
    public Long zCount(K key, double min, double max) {
        return this.opsForZSet().count(this.namingKey(key), min, max);
    }

    /**
     * Increment the score of element with {@code member} in sorted set by {@code score}.
     *
     * @param key must not be {@literal null}.
     * @param member the member.
     * @param score
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
     */
    @Override
    public Double zIncrby(K key, V member, double score) {
        return this.opsForZSet().incrementScore(this.namingKey(key), member, score);
    }

    /**
     * Get elements between {@code start} and {@code end} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     */
    @Override
    public Set<V> zRange(K key, long start, long end) {
        return this.opsForZSet().range(this.namingKey(key), start, end);
    }

    /**
     * Get elements in range from {@code start} to {@code end} from sorted set ordered from high to low.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     */
    @Override
    public Set<V> zRevRange(K key, long start, long end) {
        return this.opsForZSet().reverseRange(this.namingKey(key), start, end);
    }

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    @Override
    public Set<V> zRangeByScore(K key, double min, double max) {
        return this.opsForZSet().rangeByScore(this.namingKey(key), min, max);
    }

    /**
     * Determine the index of element with {@code value} in a sorted set.
     *
     * @param key must not be {@literal null}.
     * @param member the member.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrank">Redis Documentation: ZRANK</a>
     */
    @Override
    public Long zRank(K key, Object member) {
        return this.opsForZSet().rank(this.namingKey(key), member);
    }

    /**
     * Determine the index of element with {@code member} in a sorted set when scored high to low.
     *
     * @param key must not be {@literal null}.
     * @param member the member.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
     */
    @Override
    public Long zRevRank(K key, Object member) {
        return this.opsForZSet().reverseRank(this.namingKey(key), member);
    }

    /**
     * Remove {@code values} from sorted set. Return number of removed elements.
     *
     * @param key must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrem">Redis Documentation: ZREM</a>
     */
    @Override
    public Long zRem(K key, Object... members) {
        return this.opsForZSet().remove(this.namingKey(key), members);
    }

    /**
     * Get the score of element with {@code member} from sorted set with key {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param member the member.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zscore">Redis Documentation: ZSCORE</a>
     */
    @Override
    public Double zScore(K key, Object member) {
        return this.opsForZSet().score(this.namingKey(key), member);
    }

    /**
     * Get elements in range from {@code min} to {@code max} where score is between {@code min} and {@code max} from
     * sorted set.
     *
     * @param key    must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    @Override
    public Set<V> zRangeByScore(K key, double min, double max, long offset, long count) {
        return this.opsForZSet().rangeByScore(this.namingKey(key), min, max, offset, count);
    }

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set ordered from high to low.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     */
    @Override
    public Set<V> zRevRange(K key, double min, double max) {
        return this.opsForZSet().reverseRangeByScore(this.namingKey(key), min, max);
    }

    /**
     * Get elements in range from {@code start} to {@code end} where score is between {@code min} and {@code max} from
     * sorted set ordered high -> low.
     *
     * @param key    must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
     */
    @Override
    public Set<V> zRevRangeByScore(K key, double min, double max, long offset, long count) {
        return this.opsForZSet().reverseRangeByScore(this.namingKey(key), min, max, offset, count);
    }

    /**
     * Remove elements in range between {@code start} and {@code end} from sorted set with {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
     */
    @Override
    public Long zRemRangeByRank(K key, long start, long end) {
        return this.opsForZSet().removeRange(this.namingKey(key), start, end);
    }

    /**
     * Remove elements with scores between {@code min} and {@code max} from sorted set with {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zremrangebyscore">Redis Documentation: ZREMRANGEBYSCORE</a>
     */
    @Override
    public Long zRemRangeByScore(K key, double min, double max) {
        return this.opsForZSet().removeRangeByScore(this.namingKey(key), min, max);
    }

    /**
     * Union sorted sets at {@code key} and {@code otherKeys} and store result in destination {@code destKey}.
     *
     * @param key      must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @param destKey  must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
     */
    @Override
    public Long zUnionStore(K key, K otherKey, K destKey) {
        return this.opsForZSet().unionAndStore(this.namingKey(key), this.namingKey(otherKey), this.namingKey(destKey));
    }

    /**
     * Union sorted sets at {@code key} and {@code otherKeys} and store result in destination {@code destKey}.
     *
     * @param key       must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey   must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
     */
    @Override
    public Long zUnionStore(K key, Collection<K> otherKeys, K destKey) {
        return this.opsForZSet().unionAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey));
    }

    /**
     * Union sorted sets at {@code key} and {@code otherKeys} and store result in destination {@code destKey}.
     *
     * @param key       must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey   must not be {@literal null}.
     * @param aggregate must not be {@literal null}.
     * @param weights   must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
     */
    @Override
    public Long zUnionStore(K key, Collection<K> otherKeys, K destKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return this.opsForZSet().unionAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey), aggregate, weights);
    }

    /**
     * Intersect sorted sets at {@code key} and {@code otherKey} and store result in destination {@code destKey}.
     *
     * @param key      must not be {@literal null}.
     * @param otherKey must not be {@literal null}.
     * @param destKey  must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
     */
    @Override
    public Long zInterStore(K key, K otherKey, K destKey) {
        return this.opsForZSet().intersectAndStore(this.namingKey(key), this.namingKey(otherKey), this.namingKey(destKey));
    }

    /**
     * Intersect sorted sets at {@code key} and {@code otherKeys} and store result in destination {@code destKey}.
     *
     * @param key       must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey   must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
     */
    @Override
    public Long zInterStore(K key, Collection<K> otherKeys, K destKey) {
        return this.opsForZSet().intersectAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey));
    }

    /**
     * Intersect sorted sets at {@code key} and {@code otherKeys} and store result in destination {@code destKey}.
     *
     * @param key       must not be {@literal null}.
     * @param otherKeys must not be {@literal null}.
     * @param destKey   must not be {@literal null}.
     * @param aggregate must not be {@literal null}.
     * @param weights   must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
     */
    @Override
    public Long zInterStore(K key, Collection<K> otherKeys, K destKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return this.opsForZSet().intersectAndStore(this.namingKey(key), this.namingKeys(otherKeys), this.namingKey(destKey), aggregate, weights);
    }

    /**
     * Iterate over elements in zset at {@code key}. <br />
     * <strong>Important:</strong> Call {@link Cursor#close()} when done to avoid resource leak.
     *
     * @param key
     * @param options
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zscan">Redis Documentation: ZSCAN</a>
     */
    @Override
    public Cursor<ZSetOperations.TypedTuple<V>> zScan(K key, ScanOptions options) {
        return this.opsForZSet().scan(this.namingKey(key), options);
    }

    /**
     * Get all elements with lexicographical ordering from {@literal ZSET} at {@code key} with a value between
     * {@link RedisZSetCommands.Range#getMin()} and {@link RedisZSetCommands.Range#getMax()}.
     *
     * @param key   must not be {@literal null}.
     * @param range must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
     * @since 1.7
     */
    @Override
    public Set<V> zRangeByLex(K key, RedisZSetCommands.Range range) {
        return this.opsForZSet().rangeByLex(this.namingKey(key), range);
    }

    /**
     * Get all elements {@literal n} elements, where {@literal n = } {@link RedisZSetCommands.Limit#getCount()}, starting at
     * {@link RedisZSetCommands.Limit#getOffset()} with lexicographical ordering from {@literal ZSET} at {@code key} with a value between
     * {@link RedisZSetCommands.Range#getMin()} and {@link RedisZSetCommands.Range#getMax()}.
     *
     * @param key   must not be {@literal null}
     * @param range must not be {@literal null}.
     * @param limit can be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
     */
    @Override
    public Set<V> zRangeByLex(K key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return this.opsForZSet().rangeByLex(this.namingKey(key), range, limit);
    }

    /**
     * Add {@link Point} with given member {@literal name} to {@literal key}.
     *
     * @param key    must not be {@literal null}.
     * @param point  must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Override
    public Long geoAdd(K key, Point point, V member) {
        return this.opsForGeo().add(this.namingKey(key), point, member);
    }

    /**
     * Add {@link RedisGeoCommands.GeoLocation} to {@literal key}.
     *
     * @param key      must not be {@literal null}.
     * @param location must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Override
    public Long geoAdd(K key, RedisGeoCommands.GeoLocation<V> location) {
        return this.opsForGeo().add(this.namingKey(key), location);
    }

    /**
     * Add {@link Map} of member / {@link Point} pairs to {@literal key}.
     *
     * @param key                 must not be {@literal null}.
     * @param memberCoordinateMap must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Override
    public Long geoAdd(K key, Map<V, Point> memberCoordinateMap) {
        return this.opsForGeo().add(this.namingKey(key), memberCoordinateMap);
    }

    /**
     * Add {@link RedisGeoCommands.GeoLocation}s to {@literal key}
     *
     * @param key          must not be {@literal null}.
     * @param geoLocations must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Override
    public Long geoAdd(K key, Iterable<RedisGeoCommands.GeoLocation<V>> geoLocations) {
        return this.opsForGeo().add(this.namingKey(key), geoLocations);
    }

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2}.
     *
     * @param key     must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    @Override
    public Distance geoDist(K key, V member1, V member2) {
        return this.opsForGeo().distance(this.namingKey(key), member1, member2);
    }

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2} in the given {@link Metric}.
     *
     * @param key     must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @param metric  must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    @Override
    public Distance geoDist(K key, V member1, V member2, Metric metric) {
        return this.opsForGeo().distance(this.namingKey(key), member1, member2, metric);
    }

    /**
     * Get Geohash representation of the position for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
     */
    @Override
    public List<String> geoHash(K key, V... members) {
        return this.opsForGeo().hash(this.namingKey(key), members);
    }

    /**
     * Get the {@link Point} representation of positions for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
     */
    @Override
    public List<Point> geoPos(K key, V... members) {
        return this.opsForGeo().position(this.namingKey(key), members);
    }

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> geoRadius(K key, Circle within) {
        return this.opsForGeo().radius(this.namingKey(key), within);
    }

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle} applying {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @param args   must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> geoRadius(K key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return this.opsForGeo().radius(this.namingKey(key), within, args);
    }

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius}.
     *
     * @param key    must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param radius
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> geoRadiusByMember(K key, V member, double radius) {
        return this.opsForGeo().radius(this.namingKey(key), member, radius);
    }

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric}.
     *
     * @param key      must not be {@literal null}.
     * @param member   must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> geoRadiusByMember(K key, V member, Distance distance) {
        return this.opsForGeo().radius(this.namingKey(key), member, distance);
    }

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric} and {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key      must not be {@literal null}.
     * @param member   must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @param args     must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> geoRadiusByMember(K key, V member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return this.opsForGeo().radius(this.namingKey(key), member, distance, args);
    }

    /**
     * Remove the {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return Number of elements removed. {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Long geoRemove(K key, V... members) {
        return this.opsForGeo().remove(this.namingKey(key), members);
    }

    /**
     * Adds the given {@literal values} to the {@literal key}.
     *
     * @param key    must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return 1 of at least one of the values was added to the key; 0 otherwise. {@literal null} when used in pipeline /
     * transaction.
     * @see <a href="https://redis.io/commands/pfadd">Redis Documentation: PFADD</a>
     */
    @Override
    public Long pfAdd(K key, V... values) {
        return this.opsForHyperLogLog().add(this.namingKey(key), values);
    }

    /**
     * Gets the current number of elements within the {@literal key}.
     *
     * @param keys must not be {@literal null} or {@literal empty}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/pfcount">Redis Documentation: PFCOUNT</a>
     */
    @Override
    public Long pfCount(K... keys) {
        return this.opsForHyperLogLog().size(this.namingKeys(keys));
    }

    /**
     * Merges all values of given {@literal sourceKeys} into {@literal destination} key.
     *
     * @param destination key of HyperLogLog to move source keys into.
     * @param sourceKeys  must not be {@literal null} or {@literal empty}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/pfmerge">Redis Documentation: PFMERGE</a>
     */
    @Override
    public Long pfMerge(K destination, K... sourceKeys) {
        return this.opsForHyperLogLog().union(this.namingKey(destination), this.namingKeys(sourceKeys));
    }

    /**
     * Unlink the {@code key} from the keyspace. Unlike with {@link IValueCommands#del(Object)} the actual memory reclaiming here
     * happens asynchronously.
     *
     * @param key must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: UNLINK</a>
     * @since 2.1
     */
    @Override
    public Boolean unlink(K key) {
        return this.redisTemplate.unlink(this.namingKey(key));
    }

    /**
     * Unlink the {@code keys} from the keyspace. Unlike with {@link IValueCommands#del(Collection)} the actual memory reclaiming
     * here happens asynchronously.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: UNLINK</a>
     * @since 2.1
     */
    @Override
    public Long unlink(Collection<K> keys) {
        return this.redisTemplate.unlink(this.namingKeys(keys));
    }

    /**
     * Move given {@code key} to database with {@code index}.
     *
     * @param key     must not be {@literal null}.
     * @param dbIndex
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/move">Redis Documentation: MOVE</a>
     */
    @Override
    public Boolean move(K key, int dbIndex) {
        return this.redisTemplate.move(this.namingKey(key), dbIndex);
    }

    /**
     * Sort the elements for {@code query}.
     *
     * @param query must not be {@literal null}.
     * @return the results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public List<V> sort(SortQuery<K> query) {
        return this.redisTemplate.sort(query);
    }

    /**
     * Sort the elements for {@code query} applying {@link RedisSerializer}.
     *
     * @param query            must not be {@literal null}.
     * @param resultSerializer
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer) {
        return this.redisTemplate.sort(query, resultSerializer);
    }

    /**
     * Sort the elements for {@code query} applying {@link BulkMapper}.
     *
     * @param query      must not be {@literal null}.
     * @param bulkMapper
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper) {
        return this.redisTemplate.sort(query, bulkMapper);
    }

    /**
     * Sort the elements for {@code query} applying {@link BulkMapper} and {@link RedisSerializer}.
     *
     * @param query            must not be {@literal null}.
     * @param bulkMapper
     * @param resultSerializer
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        return this.redisTemplate.sort(query, bulkMapper, resultSerializer);
    }

    /**
     * Sort the elements for {@code query} and store result in {@code storeKey}.
     *
     * @param query    must not be {@literal null}.
     * @param storeKey must not be {@literal null}.
     * @return number of values. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public Long sort(SortQuery<K> query, K storeKey) {
        return this.redisTemplate.sort(query, storeKey);
    }

    /**
     * Executes a Redis session. Allows multiple operations to be executed in the same session enabling 'transactional'
     * capabilities through {@link #multi()} and {@link #watch(Collection)} operations.
     *
     * @param session session callback. Must not be {@literal null}.
     * @return result object returned by the action or <tt>null</tt>
     */
    @Override
    public <T> T execute(SessionCallbackX<T> session) {
        DefaultSessionCallback<T> defaultSessionCallback = new DefaultSessionCallback<>(session);
        return this.redisTemplate.execute(defaultSessionCallback);
    }

    /**
     * Executes the given Redis session on a pipelined connection. Allows transactions to be pipelined. Note that the
     * callback <b>cannot</b> return a non-null value as it gets overwritten by the pipeline.
     *
     * @param session Session callback
     * @return list of objects returned by the pipeline
     */
    @Override
    public List<Object> executePipelined(SessionCallbackX<?> session) {
        DefaultSessionCallback<?> defaultSessionCallback = new DefaultSessionCallback<>(session);
        return this.redisTemplate.executePipelined(defaultSessionCallback);
    }

    /**
     * Executes the given Redis session on a pipelined connection, returning the results using a dedicated serializer.
     * Allows transactions to be pipelined. Note that the callback <b>cannot</b> return a non-null value as it gets
     * overwritten by the pipeline.
     *
     * @param session          Session callback
     * @param resultSerializer
     * @return list of objects returned by the pipeline
     */
    @Override
    public List<Object> executePipelined(SessionCallbackX session, RedisSerializer<?> resultSerializer) {
        DefaultSessionCallback<?> defaultSessionCallback = new DefaultSessionCallback<>(session);
        return this.redisTemplate.executePipelined(defaultSessionCallback, resultSerializer);
    }

    /**
     * Watch given {@code key} for modifications during transaction started with {@link #multi()}.
     *
     * @param key must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    @Override
    public void watch(K key) {
        this.redisTemplate.watch(key);
    }

    /**
     * Watch given {@code keys} for modifications during transaction started with {@link #multi()}.
     *
     * @param keys must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    @Override
    public void watch(Collection<K> keys) {
        this.redisTemplate.watch(keys);
    }

    /**
     * Flushes all the previously {@link #watch(Object)} keys.
     *
     * @see <a href="https://redis.io/commands/unwatch">Redis Documentation: UNWATCH</a>
     */
    @Override
    public void unwatch() {
        this.redisTemplate.unwatch();
    }

    /**
     * Mark the start of a transaction block. <br>
     * Commands will be queued and can then be executed by calling {@link #exec()} or rolled back using {@link #discard()}
     * <p>
     *
     * @see <a href="https://redis.io/commands/multi">Redis Documentation: MULTI</a>
     */
    @Override
    public void multi() {
        this.redisTemplate.multi();
    }

    /**
     * Discard all commands issued after {@link #multi()}.
     *
     * @see <a href="https://redis.io/commands/discard">Redis Documentation: DISCARD</a>
     */
    @Override
    public void discard() {
        this.redisTemplate.discard();
    }

    /**
     * Executes all queued commands in a transaction started with {@link #multi()}. <br>
     * If used along with {@link #watch(Object)} the operation will fail if any of watched keys has been modified.
     *
     * @return List of replies for each executed command.
     * @see <a href="https://redis.io/commands/exec">Redis Documentation: EXEC</a>
     */
    @Override
    public List<Object> exec() {
        return this.redisTemplate.exec();
    }

    /**
     * Execute a transaction, using the provided {@link RedisSerializer} to deserialize any results that are byte[]s or
     * Collections of byte[]s. If a result is a Map, the provided {@link RedisSerializer} will be used for both the keys
     * and values. Other result types (Long, Boolean, etc) are left as-is in the converted results. Tuple results are
     * automatically converted to TypedTuples.
     *
     * @param valueSerializer The {@link RedisSerializer} to use for deserializing the results of transaction exec
     * @return The deserialized results of transaction exec
     */
    @Override
    public List<Object> exec(RedisSerializer<?> valueSerializer) {
        return this.redisTemplate.exec(valueSerializer);
    }
}
