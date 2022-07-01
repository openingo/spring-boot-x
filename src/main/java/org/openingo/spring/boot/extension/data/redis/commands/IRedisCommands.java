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

import org.openingo.spring.boot.extension.data.redis.callback.SessionCallbackX;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * IRedisCommands
 *
 * @author Qicz
 */
public interface IRedisCommands<K, V> extends
        IValueCommands<K, V>,
        IHashCommands<K, V>,
        IListCommands<K, V>,
        ISetCommands<K, V>,
        IZSetCommands<K, V>,
        IGeoCommands<K, V>,
        IHyperLogLogCommands<K, V>,
        IRedisTransactionsCommands<K, V> {

    /**
     * Unlink the {@code key} from the keyspace. Unlike with {@link IValueCommands#del(Object)} the actual memory reclaiming here
     * happens asynchronously.
     *
     * @param key must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: UNLINK</a>
     * @since 2.1
     */
    @Nullable
    Boolean unlink(K key);

    /**
     * Unlink the {@code keys} from the keyspace. Unlike with {@link IValueCommands#del(Collection)} the actual memory reclaiming
     * here happens asynchronously.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: UNLINK</a>
     * @since 2.1
     */
    @Nullable
    Long unlink(Collection<K> keys);

    /**
     * Move given {@code key} to database with {@code index}.
     *
     * @param key must not be {@literal null}.
     * @param dbIndex
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/move">Redis Documentation: MOVE</a>
     */
    @Nullable
    Boolean move(K key, int dbIndex);

    /**
     * Sort the elements for {@code query}.
     *
     * @param query must not be {@literal null}.
     * @return the results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Nullable
    List<V> sort(SortQuery<K> query);

    /**
     * Sort the elements for {@code query} applying {@link RedisSerializer}.
     *
     * @param query must not be {@literal null}.
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Nullable
    <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer);

    /**
     * Sort the elements for {@code query} applying {@link BulkMapper}.
     *
     * @param query must not be {@literal null}.
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Nullable
    <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper);

    /**
     * Sort the elements for {@code query} applying {@link BulkMapper} and {@link RedisSerializer}.
     *
     * @param query must not be {@literal null}.
     * @return the deserialized results of sort. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Nullable
    <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer);

    /**
     * Sort the elements for {@code query} and store result in {@code storeKey}.
     *
     * @param query must not be {@literal null}.
     * @param storeKey must not be {@literal null}.
     * @return number of values. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    @Nullable
    Long sort(SortQuery<K> query, K storeKey);

    /**
     * Executes a Redis session. Allows multiple operations to be executed in the same session enabling 'transactional'
     * capabilities through {@link #multi()} and {@link #watch(Collection)} operations.
     *
     * @param <T> return type
     * @param session session callback. Must not be {@literal null}.
     * @return result object returned by the action or <tt>null</tt>
     */
    @Nullable
    <T> T execute(SessionCallbackX<T> session);

    /**
     * Executes the given Redis session on a pipelined connection. Allows transactions to be pipelined. Note that the
     * callback <b>cannot</b> return a non-null value as it gets overwritten by the pipeline.
     *
     * @param session Session callback
     * @return list of objects returned by the pipeline
     */
    List<Object> executePipelined(final SessionCallbackX<?> session);

    /**
     * Executes the given Redis session on a pipelined connection, returning the results using a dedicated serializer.
     * Allows transactions to be pipelined. Note that the callback <b>cannot</b> return a non-null value as it gets
     * overwritten by the pipeline.
     *
     * @param session Session callback
     * @param resultSerializer
     * @return list of objects returned by the pipeline
     */
    List<Object> executePipelined(final SessionCallbackX session, final RedisSerializer<?> resultSerializer);
}
