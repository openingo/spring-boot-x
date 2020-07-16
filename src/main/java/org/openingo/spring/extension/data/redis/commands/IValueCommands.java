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

package org.openingo.spring.extension.data.redis.commands;

import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * IValueCommands
 *
 * @author Qicz
 */
public interface IValueCommands<K, V> {

    /**
     * Set {@code value} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    void set(K key, V value);

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param timeout the key expiration timeout.
     * @see <a href="https://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    void setEx(K key, long timeout, V value);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    @Nullable
    V get(K key);

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Nullable
    Boolean del(K key);

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    @Nullable
    Long del(Collection<K> keys);

    /**
     * Set multiple keys to multiple values using key-value pairs.
     *
     * "key1", "value1", "key2", "values"
     *
     * @param keysValues must not be {@literal null}.
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    void mSet(Object... keysValues);

    /**
     * Set multiple keys to multiple values using key-value pairs provided in {@code tuple}.
     *
     * @param map must not be {@literal null}.
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    void mSet(Map<K, V> map);

    /**
     * Get multiple {@code keys}. Values are returned in the order of the requested keys.
     *
     * @param keys must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    @Nullable
    List<V> mGet(Collection<K> keys);

    /**
     * Decrement an integer value stored as string value under {@code key} by one.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/decr">Redis Documentation: DECR</a>
     */
    @Nullable
    Long decr(K key);

    /**
     * Decrement an integer value stored as string value under {@code key} by {@code delta}.
     *
     * @param key must not be {@literal null}.
     * @param delta
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/decrby">Redis Documentation: DECRBY</a>
     */
    @Nullable
    Long decrBy(K key, long delta);

    /**
     * Increment an integer value stored as string value under {@code key} by one.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/incr">Redis Documentation: INCR</a>
     */
    @Nullable
    Long incr(K key);

    /**
     * Increment an integer value stored as string value under {@code key} by {@code delta}.
     *
     * @param key must not be {@literal null}.
     * @param delta
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     */
    @Nullable
    Long incrBy(K key, long delta);

    /**
     * Determine if given {@code key} exists.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Nullable
    Boolean exists(K key);

    /**
     * Rename key {@code oldKey} to {@code newKey}.
     *
     * @param oldKey must not be {@literal null}.
     * @param newKey must not be {@literal null}.
     * @see <a href="https://redis.io/commands/rename">Redis Documentation: RENAME</a>
     */
    void rename(K oldKey, K newKey);

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key must not be {@literal null}.
     * @param timeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Nullable
    Boolean expire(K key, long timeoutSeconds);

    /**
     * Set the expiration for given {@code key} as a {@literal date} timestamp.
     *
     * @param key must not be {@literal null}.
     * @param date must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Nullable
    Boolean expireAt(K key, Date date);

    /**
     * Set time(Seconds) to live for given {@code key}..
     *
     * @param key must not be {@literal null}.
     * @param millTimeoutSeconds
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Nullable
    Boolean pExpire(K key, long millTimeoutSeconds);

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getset">Redis Documentation: GETSET</a>
     */
    @Nullable
    V getSet(K key, V value);

    /**
     * Remove the expiration from given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     */
    @Nullable
    Boolean persist(K key);

    /**
     * Determine the type stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/type">Redis Documentation: TYPE</a>
     */
    @Nullable
    DataType type(K key);

    /**
     * Get the time to live for {@code key} in seconds.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/ttl">Redis Documentation: TTL</a>
     */
    @Nullable
    Long ttl(K key);

    /**
     * Count the number of {@code keys} that exist.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys existing among the ones specified as arguments. Keys mentioned multiple times and
     *         existing are counted multiple times.
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    @Nullable
    Long countExistingKeys(Collection<K> keys);

    /**
     * Append a {@code value} to {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/append">Redis Documentation: APPEND</a>
     */
    @Nullable
    Integer append(K key, String value);

    /**
     * Get a substring of value of {@code key} between {@code begin} and {@code end}.
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     */
    @Nullable
    String getRange(K key, long start, long end);

    /**
     * Overwrite parts of {@code key} starting at the specified {@code offset} with given {@code value}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @param offset
     * @see <a href="https://redis.io/commands/setrange">Redis Documentation: SETRANGE</a>
     */
    void setRange(K key, V value, long offset);

    /**
     * Get the length of the value stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     */
    @Nullable
    Long strLen(K key);

    /**
     * Sets the bit at {@code offset} in value stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param offset
     * @param value
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/setbit">Redis Documentation: SETBIT</a>
     */
    @Nullable
    Boolean setBit(K key, long offset, boolean value);

    /**
     * Get the bit value at {@code offset} of value at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param offset
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/getbit">Redis Documentation: GETBIT</a>
     */
    @Nullable
    Boolean getBit(K key, long offset);

    /**
     * Get / Manipulate specific integer fields of varying bit widths and arbitrary non (necessary) aligned offset stored
     * at a given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param subCommands must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @since 2.1
     * @see <a href="https://redis.io/commands/bitfield">Redis Documentation: BITFIELD</a>
     */
    @Nullable
    List<Long> bitField(K key, BitFieldSubCommands subCommands);
}
