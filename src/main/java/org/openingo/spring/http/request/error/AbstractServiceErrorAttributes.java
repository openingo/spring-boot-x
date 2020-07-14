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

import org.openingo.jdkits.http.RespData;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.exception.ServiceException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributesX;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * AbstractServiceErrorAttributes
 *
 * @author Qicz
 */
public abstract class AbstractServiceErrorAttributes extends DefaultErrorAttributesX {

    /**
     * Create a new {@link AbstractServiceErrorAttributes} instance that included the
     * "exception" attribute , can get the "exception" instance.
     */
    public AbstractServiceErrorAttributes() {
        super(true);
    }

    /**
     * Returns a {@link Map} of the error attributes. The map can be used as the model of
     * an error page {@link ModelAndView}, or returned as a {@link ResponseBody}.
     *
     * @param webRequest        the source request
     * @param includeStackTrace if stack trace elements should be included
     * @return a map of services error attributes
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        // processing super error attributes
        Object code = this.getStatus(errorAttributes);
        String message = this.getError(errorAttributes);
        Exception exception = this.getHandlerExecutionException();
        Map<String, Object> serviceErrorAttributes = new HashMap<>();
        if (!this.responseOK(errorAttributes)
                && ValidateKit.isNotNull(exception)) {
            Object decorateExceptionCode = this.decorateExceptionCode(exception);
            if (ValidateKit.isNotNull(decorateExceptionCode)) {
                code = decorateExceptionCode;
            }
            // check exception instance type again
            if (exception instanceof ServiceException) {
                code = ((ServiceException) exception).getExceptionCode();
            }
            message = this.decorateExceptionMessage(exception);
            this.decorateErrorAttributes(errorAttributes, serviceErrorAttributes);
        }
        // services error attributes processing
        if (!RespData.Config.SM_ONLY) {
            // deduce code type by RespData.Config.FAILURE_SC
            if (RespData.Config.FAILURE_SC instanceof String) {
                code = code.toString();
            }
            serviceErrorAttributes.put(RespData.Config.SC_KEY, code);
        }
        serviceErrorAttributes.put(RespData.Config.SM_KEY, message);
        return serviceErrorAttributes;
    }

    /**
     * Decorate exception, may be you can returns friendly message to user.
     *
     * @param exception  the exception that got thrown during handler execution
     */
    private String decorateExceptionMessage(Exception exception) {
        String friendlyFailureMessage = RespData.Config.FRIENDLY_FAILURE_MESSAGE;
        if (ValidateKit.isNotNull(friendlyFailureMessage)) {
            return friendlyFailureMessage;
        }
        return exception.getMessage();
    }

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
     * @param exception  the exception that got thrown during handler execution
     */
    public abstract Object decorateExceptionCode(Exception exception);

    /**
     * Decorate error attributes, add extension attributes etc.
     *
     * @param errorAttributes error attributes
     * @param serviceErrorAttributes service error attributes
     */
    public abstract void decorateErrorAttributes(Map<String, Object> errorAttributes,
                                                 Map<String, Object> serviceErrorAttributes);
}
