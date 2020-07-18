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

package org.openingo.x;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openingo.spring.http.request.error.DefaultServiceErrorAttributes;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

/**
 * BusinessErrorAttributes
 *
 * @author Qicz
 */
@Component
public class BusinessErrorAttributes extends DefaultServiceErrorAttributes {

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
        if (exception instanceof IndexOutOfBoundsException) {
            return 123;
        }
        if (exception instanceof JsonProcessingException) {
            return 345;
        }
        if (exception instanceof MethodArgumentNotValidException
                || exception instanceof ConstraintViolationException
                || exception instanceof BindException
                || exception instanceof HttpMessageNotReadableException
                || exception instanceof MissingServletRequestPartException
                || exception instanceof MissingServletRequestParameterException
                || exception instanceof MultipartException) {
            return 1234;
        }
        return super.decorateExceptionCode(exception);
    }

    /**
     * Decorate error attributes, add extension attributes etc.
     *
     * @param errorAttributes        error attributes
     * @param serviceErrorAttributes service error attributes
     */
    @Override
    public void decorateErrorAttributes(Map<String, Object> errorAttributes, Map<String, Object> serviceErrorAttributes) {
        super.decorateErrorAttributes(errorAttributes, serviceErrorAttributes);
        //serviceErrorAttributes.putAll(errorAttributes);
    }
}
