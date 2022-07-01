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

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * IGeoCommands
 *
 * @author Qicz
 */
public interface IGeoCommands<K, M> {

    /**
     * Add {@link Point} with given member {@literal name} to {@literal key}.
     *
     * @param key must not be {@literal null}.
     * @param point must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Nullable
    Long geoAdd(K key, Point point, M member);

    /**
     * Add {@link RedisGeoCommands.GeoLocation} to {@literal key}.
     *
     * @param key must not be {@literal null}.
     * @param location must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Nullable
    Long geoAdd(K key, RedisGeoCommands.GeoLocation<M> location);

    /**
     * Add {@link Map} of member / {@link Point} pairs to {@literal key}.
     *
     * @param key must not be {@literal null}.
     * @param memberCoordinateMap must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Nullable
    Long geoAdd(K key, Map<M, Point> memberCoordinateMap);

    /**
     * Add {@link RedisGeoCommands.GeoLocation}s to {@literal key}
     *
     * @param key must not be {@literal null}.
     * @param locations must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    @Nullable
    Long geoAdd(K key, Iterable<RedisGeoCommands.GeoLocation<M>> locations);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2}.
     *
     * @param key must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    @Nullable
    Distance geoDist(K key, M member1, M member2);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2} in the given {@link Metric}.
     *
     * @param key must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @param metric must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    @Nullable
    Distance geoDist(K key, M member1, M member2, Metric metric);

    /**
     * Get Geohash representation of the position for one or more {@literal member}s.
     *
     * @param key must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
     */
    @Nullable
    List<String> geoHash(K key, M... members);

    /**
     * Get the {@link Point} representation of positions for one or more {@literal member}s.
     *
     * @param key must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
     */
    @Nullable
    List<Point> geoPos(K key, M... members);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle}.
     *
     * @param key must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    @Nullable
    GeoResults<RedisGeoCommands.GeoLocation<M>> geoRadius(K key, Circle within);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle} applying {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @param args must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    @Nullable
    GeoResults<RedisGeoCommands.GeoLocation<M>> geoRadius(K key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius}.
     *
     * @param key must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param radius
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Nullable
    GeoResults<RedisGeoCommands.GeoLocation<M>> geoRadiusByMember(K key, M member, double radius);


    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric}.
     *
     * @param key must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Nullable
    GeoResults<RedisGeoCommands.GeoLocation<M>> geoRadiusByMember(K key, M member, Distance distance);


    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric} and {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @param args must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    @Nullable
    GeoResults<RedisGeoCommands.GeoLocation<M>> geoRadiusByMember(K key, M member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args);

    /**
     * Remove the {@literal member}s.
     *
     * @param key must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return Number of elements removed. {@literal null} when used in pipeline / transaction.
     */
    @Nullable
    Long geoRemove(K key, M... members);
}
