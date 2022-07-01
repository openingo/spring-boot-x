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

package org.openingo.spring.boot.extension.gedid.engine;

/**
 * IDidEngine
 *
 * @author Qicz
 * @since 2021/6/25 10:48
 */
public interface IDidEngine<T> {

	String GEDID = "gedid";

	/**
	 * Follow The business with name.
	 * @param businessName business name
	 * @param startId the first id
	 */
	void follow(String businessName, T startId);

	/**
	 * get the embellished business name
	 * @param businessName the business name
	 * @return embellished business name
	 */
	default String getEmbellishedName(String businessName) {
		return businessName;
	}

	/**
	 * get fixed start id
	 *
	 * @param startId not all startId
	 * @return {@link T} default return startId self
	 */
	default T getFixedStartId(T startId) {
		return startId;
	}

	/**
	 * the next id
	 * @param businessName the business name
	 * @return Next id.
	 */
	T next(String businessName);

	/**
	 * throw onw unsupportedOperationException
	 * @param operation unsupported operation
	 */
	default void unsupportedOperation(String operation) {
		throw new UnsupportedOperationException(String.format("`%s` : The `%s` Operation is not supported!", this.engineName(), operation));
	}

	/**
	 * current engine's name
	 * @return the engine name
	 */
	String engineName();
}
