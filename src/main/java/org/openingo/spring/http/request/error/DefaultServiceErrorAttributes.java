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

package org.openingo.spring.http.request.error;

import java.util.Map;

/**
 * DefaultServiceErrorAttributes
 *
 * @author Qicz
 */
public class DefaultServiceErrorAttributes extends AbstractServiceErrorAttributes {

    /**
     * Decorate exception error code, custom for your business logic.
     * <code>
     * <pre>
     * public Object decorateExceptionCode(Exception exception) {
     *    if (exception instanceof IndexOutOfBoundsException) {
     *      return 123;
     *    }
     *   return super.decorateExceptionCode(exception);
     * }
     * </pre>
     * </code>
     *
     * @param exception the exception that got thrown during handler execution
     */
    @Override
    public Object decorateExceptionCode(Exception exception) {
        return null;
    }

    /**
     * Decorate error attributes, add extension attributes etc.
     * the {@code errorAttributes} that has exception, handler, message,
     * error, timestamp, status, path params.
     *
     * @param errorAttributes        error attributes
     * @param serviceErrorAttributes service error attributes
     */
    @Override
    public void decorateErrorAttributes(Map<String, Object> errorAttributes, Map<String, Object> serviceErrorAttributes) {

    }
}
