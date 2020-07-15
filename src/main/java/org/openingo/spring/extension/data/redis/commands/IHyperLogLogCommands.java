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

/**
 * IHyperLogLogCommands
 *
 * @author Qicz
 */
public interface IHyperLogLogCommands<K, V> {

	/**
	 * Adds the given {@literal values} to the {@literal key}.
	 *
	 * @param key must not be {@literal null}.
	 * @param values must not be {@literal null}.
	 * @return 1 of at least one of the values was added to the key; 0 otherwise. {@literal null} when used in pipeline /
	 *         transaction.
	 * @see <a href="https://redis.io/commands/pfadd">Redis Documentation: PFADD</a>
	 */
	Long pfAdd(K key, V... values);

	/**
	 * Gets the current number of elements within the {@literal key}.
	 *
	 * @param keys must not be {@literal null} or {@literal empty}.
	 * @return {@literal null} when used in pipeline / transaction.
	 * @see <a href="https://redis.io/commands/pfcount">Redis Documentation: PFCOUNT</a>
	 */
	Long pfCount(K... keys);

	/**
	 * Merges all values of given {@literal sourceKeys} into {@literal destination} key.
	 *
	 * @param destination key of HyperLogLog to move source keys into.
	 * @param sourceKeys must not be {@literal null} or {@literal empty}.
	 * @return {@literal null} when used in pipeline / transaction.
	 * @see <a href="https://redis.io/commands/pfmerge">Redis Documentation: PFMERGE</a>
	 */
	Long pfMerge(K destination, K... sourceKeys);
}