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

package org.openingo.spring.boot.extension.http.request.error;

import org.openingo.jdkits.http.RespData;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.boot.exception.ServiceException;
import org.openingo.spring.boot.extension.http.config.HttpRequestErrorConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * AbstractServiceErrorAttributes
 *
 * @author Qicz
 */
public abstract class AbstractServiceErrorAttributes extends DefaultErrorAttributesX {

    @Autowired
    HttpRequestErrorConfigProperties httpRequestErrorConfigProperties;

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
        // error resolve config is disable using the origin errorAttributes
        if (!this.httpRequestErrorConfigProperties.isEnable()) {
            return errorAttributes;
        }
        // error code prepare
        Object code = null;
        // processing super error attributes
        String message = null;
        Exception exception = this.getHandlerExecutionException();
        Map<String, Object> serviceErrorAttributes = new HashMap<>();
        if (!this.responseOK(errorAttributes)
                && ValidateKit.isNotNull(exception)) {
            // check ServiceException with setting code
            if (exception instanceof ServiceException) {
                code = ((ServiceException) exception).getExceptionCode();
                message = exception.getMessage();
            }
            // decorate exception
            // can rewrite ServiceException using common code
            code = ValidateKit.isNull(code) ? this.decorateExceptionCode(exception) : code;
            // decorate exception message
            message = ValidateKit.isNull(message) ? this.decorateExceptionMessage0(exception) : message;
        }
        // services error attributes processing
        if (!RespData.Config.SM_ONLY) {
            // if the code not handle, using response status
            code = ValidateKit.isNull(code) ? this.getStatus(errorAttributes) : code;
            // deduce code type by RespData.Config.FAILURE_SC
            code = (RespData.Config.FAILURE_SC instanceof String) ? code.toString() : code;
            serviceErrorAttributes.put(RespData.Config.SC_KEY, code);
        }
        message = ValidateKit.isNull(message) ? this.getError(errorAttributes) : message;
        serviceErrorAttributes.put(RespData.Config.SM_KEY, message);
        this.decorateErrorAttributes(errorAttributes, serviceErrorAttributes);
        return serviceErrorAttributes;
    }

    /**
     * Decorate exception message, may be you can returns friendly message to user.
     *
     * @param exception  the exception that got thrown during handler execution
     */
    private String decorateExceptionMessage0(Exception exception) {
        String friendlyFailureMessage = RespData.Config.FRIENDLY_FAILURE_MESSAGE;
        if (ValidateKit.isNotNull(friendlyFailureMessage)) {
            return friendlyFailureMessage;
        }
        if (exception instanceof MethodArgumentNotValidException) {
            return this.getErrorMessage(((MethodArgumentNotValidException)exception).getBindingResult());
        }
        if (exception instanceof BindException) {
            return this.getErrorMessage(((BindException)exception).getBindingResult());
        }
        if (exception instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) exception).getConstraintViolations();
            if (Objects.nonNull(constraintViolations)) {
                Optional<ConstraintViolation<?>> first = constraintViolations.stream().findFirst();
                return first.map(ConstraintViolation::getMessage).get();
            }
            return exception.getMessage();
        }
        return this.decorateExceptionMessage(exception);
    }

    /**
     * fetch the bindingResult error message.
     * @param bindingResult bindingResult with errors
     * @return the error message
     */
    private String getErrorMessage(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        return errors.get(0).getDefaultMessage();
    }

    /**
     * Decorate exception message for your self.
     *
     * @param exception  the exception that got thrown during handler execution
     */
    public abstract String decorateExceptionMessage(Exception exception);

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
     * the {@code errorAttributes} that has exception, handler, message,
     * error, timestamp, status, path params.
     *
     * @param errorAttributes error attributes
     * @param serviceErrorAttributes service error attributes
     */
    public abstract void decorateErrorAttributes(Map<String, Object> errorAttributes,
                                                 Map<String, Object> serviceErrorAttributes);
}
