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

package org.openingo.spring.boot.extension.data.redis.naming;

import org.openingo.jdkits.validate.ValidateKit;

/**
 * DefaultKeyNamingPolicy
 *
 * @author Qicz
 */
public class DefaultKeyNamingPolicy implements IKeyNamingPolicy {

    /**
     * if {@code KeyNamingKit.getNaming()} is "null" return key,
     * otherwise return {@code KeyNamingKit.getNaming()}+{@code KeyNamingKit.NAMING_SEPARATOR}+key
     * @param key
     * @return wrapper key
     */
    @Override
    public String getKeyName(String key) {
        String naming = KeyNamingKit.get();
        if (ValidateKit.isNull(naming)) {
            return key;
        }
        if (!naming.endsWith(KeyNamingKit.NAMING_SEPARATOR)) {
            naming = naming + KeyNamingKit.NAMING_SEPARATOR;
        }
        return naming + key;
    }
}
