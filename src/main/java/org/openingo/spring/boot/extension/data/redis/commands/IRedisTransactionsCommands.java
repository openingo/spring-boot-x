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

package org.openingo.spring.boot.extension.data.redis.commands;

import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.List;

/**
 * IRedisTransactionsCommands
 *
 * @author Qicz
 */
public interface IRedisTransactionsCommands<K, V> {

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Transactions
    // -------------------------------------------------------------------------

    /**
     * Watch given {@code key} for modifications during transaction started with {@link #multi()}.
     *
     * @param key must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    void watch(K key);

    /**
     * Watch given {@code keys} for modifications during transaction started with {@link #multi()}.
     *
     * @param keys must not be {@literal null}.
     * @see <a href="https://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    void watch(Collection<K> keys);

    /**
     * Flushes all the previously {@link #watch(Object)} keys.
     *
     * @see <a href="https://redis.io/commands/unwatch">Redis Documentation: UNWATCH</a>
     */
    void unwatch();

    /**
     * Mark the start of a transaction block. <br>
     * Commands will be queued and can then be executed by calling {@link #exec()} or rolled back using {@link #discard()}
     * <p>
     *
     * @see <a href="https://redis.io/commands/multi">Redis Documentation: MULTI</a>
     */
    void multi();

    /**
     * Discard all commands issued after {@link #multi()}.
     *
     * @see <a href="https://redis.io/commands/discard">Redis Documentation: DISCARD</a>
     */
    void discard();

    /**
     * Executes all queued commands in a transaction started with {@link #multi()}. <br>
     * If used along with {@link #watch(Object)} the operation will fail if any of watched keys has been modified.
     *
     * @return List of replies for each executed command.
     * @see <a href="https://redis.io/commands/exec">Redis Documentation: EXEC</a>
     */
    List<Object> exec();

    /**
     * Execute a transaction, using the provided {@link RedisSerializer} to deserialize any results that are byte[]s or
     * Collections of byte[]s. If a result is a Map, the provided {@link RedisSerializer} will be used for both the keys
     * and values. Other result types (Long, Boolean, etc) are left as-is in the converted results. Tuple results are
     * automatically converted to TypedTuples.
     *
     * @param valueSerializer The {@link RedisSerializer} to use for deserializing the results of transaction exec
     * @return The deserialized results of transaction exec
     */
    List<Object> exec(RedisSerializer<?> valueSerializer);
}
