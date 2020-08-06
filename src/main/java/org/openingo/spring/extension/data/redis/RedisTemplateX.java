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

import org.openingo.spring.extension.data.redis.commands.AbstractRedisCommands;
import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.DefaultStringSortQuery;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * RedisTemplateX< String, V>
 *
 * @author Qicz
 */
@SuppressWarnings("all")
public class RedisTemplateX<V> extends AbstractRedisCommands<String, V> {

    @Autowired
    private IKeyNamingPolicy keyNamingPolicy;

    // cache singleton objects (where possible)
    private @Nullable ValueOperations<String, V> valueOps;
    private @Nullable ListOperations<String, V> listOps;
    private @Nullable SetOperations<String, V> setOps;
    private @Nullable ZSetOperations<String, V> zSetOps;
    private @Nullable GeoOperations<String, V> geoOps;
    private @Nullable HyperLogLogOperations<String, V> hllOps;

    @Override
    public ClusterOperations<String, V> opsForCluster() {
        return new DefaultClusterOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public GeoOperations<String, V> opsForGeo() {
        if (this.geoOps == null) {
            this.geoOps = new DefaultGeoOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.geoOps;
    }

    @Override
    public BoundGeoOperations<String, V> boundGeoOps(String key) {
        return new DefaultBoundGeoOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public <HK, HV> BoundHashOperations<String, HK, HV> boundHashOps(String key) {
        return new DefaultBoundOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public <HK, HV> HashOperations<String, HK, HV> opsForHash() {
        return new DefaultHashOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public HyperLogLogOperations<String, V> opsForHyperLogLog() {
        if (this.hllOps == null) {
            this.hllOps = new DefaultHyperLogLogOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.hllOps;
    }

    @Override
    public ListOperations<String, V> opsForList() {
        if (this.listOps == null) {
            this.listOps = new DefaultListOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.listOps;
    }

    @Override
    public BoundListOperations<String, V> boundListOps(String key) {
        return new DefaultBoundListOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public BoundSetOperations<String, V> boundSetOps(String key) {
        return new DefaultBoundSetOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public SetOperations<String, V> opsForSet() {
        if (this.setOps == null) {
            this.setOps = new DefaultSetOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.setOps;
    }

    @Override
    public BoundValueOperations<String, V> boundValueOps(String key) {
        return new DefaultBoundValueOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public ValueOperations<String, V> opsForValue() {
        if (this.valueOps == null) {
            this.valueOps = new DefaultValueOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.valueOps;
    }

    @Override
    public BoundZSetOperations<String, V> boundZSetOps(String key) {
        return new DefaultBoundZSetOperationsX<>(key, this.redisTemplate, this.keyNamingPolicy);
    }

    @Override
    public ZSetOperations<String, V> opsForZSet() {
        if (this.zSetOps == null) {
            this.zSetOps = new DefaultZSetOperationsX<>(this.redisTemplate, this.keyNamingPolicy);
        }
        return this.zSetOps;
    }

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Override
    public Boolean del(String key) {
        return super.del(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Override
    public Long del(Collection<String> keys) {
        return super.del(this.keyNamingPolicy.getKeyNames(keys));
    }

    /**
     * Determine if given {@code key} exists.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Override
    public Boolean exists(String key) {
        return super.exists(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Rename key {@code oldKey} to {@code newKey}.
     *
     * @param oldKey must not be {@literal null}.
     * @param newKey must not be {@literal null}.
     * @see <a href="https://redis.io/commands/rename">Redis Documentation: RENAME</a>
     */
    @Override
    public void rename(String oldKey, String newKey) {
        super.rename(this.keyNamingPolicy.getKeyName(oldKey), this.keyNamingPolicy.getKeyName(newKey));
    }

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key            must not be {@literal null}.
     * @param timeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean expire(String key, long timeoutSeconds) {
        return super.expire(this.keyNamingPolicy.getKeyName(key), timeoutSeconds);
    }

    /**
     * Set the expiration for given {@code key} as a {@literal date} timestamp.
     *
     * @param key  must not be {@literal null}.
     * @param date must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean expireAt(String key, Date date) {
        return super.expireAt(this.keyNamingPolicy.getKeyName(key), date);
    }

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key                must not be {@literal null}.
     * @param millTimeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Override
    public Boolean pExpire(String key, long millTimeoutSeconds) {
        return super.pExpire(this.keyNamingPolicy.getKeyName(key), millTimeoutSeconds);
    }

    /**
     * Remove the expiration from given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     */
    @Override
    public Boolean persist(String key) {
        return super.persist(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Determine the type stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/type">Redis Documentation: TYPE</a>
     */
    @Override
    public DataType type(String key) {
        return super.type(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Get the time to live for {@code key} in seconds.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/ttl">Redis Documentation: TTL</a>
     */
    @Override
    public Long ttl(String key) {
        return super.ttl(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Count the number of {@code keys} that exist.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys existing among the ones specified as arguments. Keys mentioned multiple times and
     * existing are counted multiple times.
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Override
    public Long countExistingKeys(Collection<String> keys) {
        return super.countExistingKeys(this.keyNamingPolicy.getKeyNames(keys));
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
    public Boolean unlink(String key) {
        return this.redisTemplate.unlink(this.keyNamingPolicy.getKeyName(key));
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
    public Long unlink(Collection<String> keys) {
        return this.redisTemplate.unlink(this.keyNamingPolicy.getKeyNames(keys));
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
    public Boolean move(String key, int dbIndex) {
        return this.redisTemplate.move(this.keyNamingPolicy.getKeyName(key), dbIndex);
    }

    /**
     * Sort the elements for {@code query}.
     *
     * @param query must not be {@literal null}.
     * @return the results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Override
    public List<V> sort(SortQuery<String> query) {
        SortQuery<String> sortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, query);
        return this.redisTemplate.sort(sortQuery);
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
    public <T> List<T> sort(SortQuery<String> query, RedisSerializer<T> resultSerializer) {
        SortQuery<String> sortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, query);
        return this.redisTemplate.sort(sortQuery, resultSerializer);
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
    public <T> List<T> sort(SortQuery<String> query, BulkMapper<T, V> bulkMapper) {
        SortQuery<String> sortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, query);
        return this.redisTemplate.sort(sortQuery, bulkMapper);
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
    public <T, S> List<T> sort(SortQuery<String> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        SortQuery<String> sortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, query);
        return this.redisTemplate.sort(sortQuery, bulkMapper, resultSerializer);
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
    public Long sort(SortQuery<String> query, String storeKey) {
        SortQuery<String> sortQuery = new DefaultStringSortQuery(this.keyNamingPolicy, query);
        return this.redisTemplate.sort(sortQuery, this.keyNamingPolicy.getKeyName(storeKey));
    }

    /**
     * Watch given {@code key} for modifications during transaction started with {@link #multi()}.
     *
     * @param key must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    @Override
    public void watch(String key) {
        this.redisTemplate.watch(this.keyNamingPolicy.getKeyName(key));
    }

    /**
     * Watch given {@code keys} for modifications during transaction started with {@link #multi()}.
     *
     * @param keys must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    @Override
    public void watch(Collection<String> keys) {
        this.redisTemplate.watch(this.keyNamingPolicy.getKeyNames(keys));
    }
}
