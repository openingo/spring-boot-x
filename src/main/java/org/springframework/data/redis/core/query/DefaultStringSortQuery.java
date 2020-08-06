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

package org.springframework.data.redis.core.query;

import org.openingo.spring.extension.data.redis.naming.IKeyNamingPolicy;
import org.springframework.data.redis.connection.SortParameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultStringSortQuery
 *
 * @author Qicz
 */
public class DefaultStringSortQuery implements SortQuery<String> {

    IKeyNamingPolicy keyNamingPolicy;

    String originKey;

    SortQuery<String> origin;

    public DefaultStringSortQuery(IKeyNamingPolicy keyNamingPolicy, SortQuery<String> origin) {
        this.keyNamingPolicy = keyNamingPolicy;
        this.origin = origin;
        this.originKey = origin.getKey();
    }

    /**
     * Return the target key for sorting.
     *
     * @return the target key
     */
    @Override
    public String getKey() {
        return this.keyNamingPolicy.getKeyName(this.originKey);
    }

    /**
     * Returns the sorting order. Can be null if nothing is specified.
     *
     * @return sorting order
     */
    @Override
    public SortParameters.Order getOrder() {
        return this.origin.getOrder();
    }

    /**
     * Indicates if the sorting is numeric (default) or alphabetical (lexicographical). Can be null if nothing is
     * specified.
     *
     * @return the type of sorting
     */
    @Override
    public Boolean isAlphabetic() {
        return this.origin.isAlphabetic();
    }

    /**
     * Returns the sorting limit (range or pagination). Can be null if nothing is specified.
     *
     * @return sorting limit/range
     */
    @Override
    public SortParameters.Range getLimit() {
        return this.origin.getLimit();
    }

    /**
     * Returns the pattern of the external key used for sorting.
     *
     * @return the external key pattern
     */
    @Override
    public String getBy() {
        return this.keyNamingPolicy.getKeyName(this.origin.getBy());
    }

    /**
     * Returns the external key(s) whose values are returned by the sort.
     *
     * @return the (list of) keys used for GET
     */
    @Override
    public List<String> getGetPattern() {
        return this.origin.getGetPattern().stream()
                .filter(pattern -> pattern.contains(this.originKey))
                .map(pattern -> pattern.replace(this.originKey, this.keyNamingPolicy.getKeyName(this.originKey)))
                .collect(Collectors.toList());
    }
}
