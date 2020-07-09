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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;

import java.util.List;
import java.util.Map;

/**
 * DefaultGeoOperationsX
 *
 * @author Qicz
 */
public class DefaultGeoOperationsX<M> extends DefaultGeoOperations<java.lang.String, M> {

    IKeyNamingPolicy keyNamingPolicy;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    public void setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
    }

    /**
     * Creates new {@link DefaultGeoOperations}.
     *
     * @param template must not be {@literal null}.
     */
    public DefaultGeoOperationsX(RedisTemplate<String, M> template) {
        super(template);
    }

    /**
     * Add {@link Point} with given member {@literal name} to {@literal key}.
     *
     * @param key    must not be {@literal null}.
     * @param point  must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @since 2.0
     */
    @Override
    public Long add(String key, Point point, M member) {
        return super.add(this.getKey(key), point, member);
    }

    /**
     * Add {@link RedisGeoCommands.GeoLocation} to {@literal key}.
     *
     * @param key      must not be {@literal null}.
     * @param location must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @since 2.0
     */
    @Override
    public Long add(String key, RedisGeoCommands.GeoLocation<M> location) {
        return super.add(this.getKey(key), location);
    }

    /**
     * Add {@link Map} of member / {@link Point} pairs to {@literal key}.
     *
     * @param key                 must not be {@literal null}.
     * @param memberCoordinateMap must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @since 2.0
     */
    @Override
    public Long add(String key, Map<M, Point> memberCoordinateMap) {
        return super.add(this.getKey(key), memberCoordinateMap);
    }

    /**
     * Add {@link RedisGeoCommands.GeoLocation}s to {@literal key}
     *
     * @param key          must not be {@literal null}.
     * @param geoLocations must not be {@literal null}.
     * @return Number of elements added. {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @since 2.0
     */
    @Override
    public Long add(String key, Iterable<RedisGeoCommands.GeoLocation<M>> geoLocations) {
        return super.add(this.getKey(key), geoLocations);
    }

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2}.
     *
     * @param key     must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="https://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     * @since 2.0
     */
    @Override
    public Distance distance(String key, M member1, M member2) {
        return super.distance(this.getKey(key), member1, member2);
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
     * @since 2.0
     */
    @Override
    public Distance distance(String key, M member1, M member2, Metric metric) {
        return super.distance(this.getKey(key), member1, member2, metric);
    }

    /**
     * Get Geohash representation of the position for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
     * @since 2.0
     */
    @Override
    public List<String> hash(String key, M... members) {
        return super.hash(this.getKey(key), members);
    }

    /**
     * Get the {@link Point} representation of positions for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
     * @since 2.0
     */
    @Override
    public List<Point> position(String key, M... members) {
        return super.position(this.getKey(key), members);
    }

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     * @since 2.0
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<M>> radius(String key, Circle within) {
        return super.radius(this.getKey(key), within);
    }

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle} applying {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @param args   must not be {@literal null}.
     * @return never {@literal null} unless used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     * @since 2.0
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<M>> radius(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return super.radius(this.getKey(key), within, args);
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
     * @since 2.0
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<M>> radius(String key, M member, double radius) {
        return super.radius(this.getKey(key), member, radius);
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
     * @since 2.0
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<M>> radius(String key, M member, Distance distance) {
        return super.radius(this.getKey(key), member, distance);
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
     * @since 2.0
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<M>> radius(String key, M member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return super.radius(this.getKey(key), member, distance, args);
    }

    /**
     * Remove the {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return Number of elements removed. {@literal null} when used in pipeline / transaction.
     * @since 2.0
     */
    @Override
    public Long remove(String key, M... members) {
        return super.remove(this.getKey(key), members);
    }
}
