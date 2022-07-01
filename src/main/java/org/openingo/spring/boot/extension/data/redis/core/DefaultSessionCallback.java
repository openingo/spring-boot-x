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

package org.openingo.spring.boot.extension.data.redis.core;

import org.openingo.spring.boot.extension.data.redis.callback.SessionCallbackX;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

/**
 * DefaultSessionCallback
 *
 * @author Qicz
 */
@SuppressWarnings("unchecked")
public class DefaultSessionCallback<T> implements SessionCallback<T> {

    private SessionCallbackX<T> sessionCallbackX;

    public DefaultSessionCallback(SessionCallbackX<T> sessionCallbackX) {
        this.sessionCallbackX = sessionCallbackX;
    }

    /**
     * Executes all the given operations inside the same session.
     *
     * @param operations Redis operations
     * @return return value
     */
    @Override
    public <K, V> T execute(RedisOperations<K, V> operations) throws DataAccessException {
        operations.multi();
        this.sessionCallbackX.execute();
        return (T)operations.exec();
    }
}
