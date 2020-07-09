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
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.Set;

/**
 * DefaultClusterOperationsX
 *
 * @author Qicz
 */
public class DefaultClusterOperationsX<V> extends DefaultClusterOperations<String, V> {

    IKeyNamingPolicy keyNamingPolicy;

    private String getKey(String key) {
        return this.keyNamingPolicy.getKeyName(key);
    }

    public void setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
        this.keyNamingPolicy = keyNamingPolicy;
    }

    /**
     * Creates new {@link DefaultClusterOperations} delegating to the given {@link RedisTemplate}.
     *
     * @param template must not be {@literal null}.
     */
    public DefaultClusterOperationsX(RedisTemplate<String, V> template) {
        super(template);
    }

    /**
     * Get all keys located at given node.
     *
     * @param node    must not be {@literal null}.
     * @param pattern
     * @return never {@literal null}.
     * @see RedisConnection#keys(byte[])
     */
    @Override
    public Set<String> keys(RedisClusterNode node, String pattern) {
        // TODO valiate
        return super.keys(node, this.getKey(pattern));
    }

}
