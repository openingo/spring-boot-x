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

package org.springframework.boot.web.servlet.error;

import lombok.extern.slf4j.Slf4j;
import org.openingo.java.lang.ThreadLocalX;
import org.openingo.jdkits.lang.ObjectKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.extension.http.config.HttpConfigProperties;
import org.openingo.spring.http.request.HttpRequestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * DefaultErrorAttributesX
 *
 * @author Qicz
 */
@Slf4j
public class DefaultErrorAttributesX extends DefaultErrorAttributes {

    @Autowired
    HttpConfigProperties.HttpRequestLogConfigProperties httpRequestLogConfigProperties;

    private final ThreadLocalX<Object> HANDLER_HOLDER = new ThreadLocalX<>();
    private final ThreadLocalX<Exception> EXCEPTION_HOLDER = new ThreadLocalX<>();

    /**
     * using exception instance or not
      */
    private final boolean usingException;

    /**
     * Create a new {@link DefaultErrorAttributesX} instance that included the
     * "exception" attribute , can not get the "exception" instance.
     */
    public DefaultErrorAttributesX() {
        this(false);
    }

    /**
     * Create a new {@link DefaultErrorAttributesX} instance.
     * default included the "exception" attribute.
     *
     * @param usingException whether to get the "exception" instance.
     */
    public DefaultErrorAttributesX(boolean usingException) {
        super(true);
        this.usingException = usingException;
    }

    /**
     * Returns a {@link Map} of the error attributes that has
     * exception, handler, message, error, timestamp, status, path params.
     * The map can be used as the model of an error page {@link ModelAndView},
     * or returned as a {@link ResponseBody}.
     * @param webRequest the source request
     * @param includeStackTrace if stack trace elements should be included
     * @return a map of error attributes
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        Object handler = this.HANDLER_HOLDER.getRemove();
        if (ValidateKit.isNotNull(handler)) {
            errorAttributes.put("handler", handler.toString());
        }
        return errorAttributes;
    }

    /**
     * Try to resolve the given exception that got thrown during handler execution,
     * returning a {@link ModelAndView} that represents a specific error page if appropriate.
     * <p>The returned {@code ModelAndView} may be {@linkplain ModelAndView#isEmpty() empty}
     * to indicate that the exception has been resolved successfully but that no view
     * should be rendered, for instance by setting a status code.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  the executed handler, or {@code null} if none chosen at the
     *                 time of the exception (for example, if multipart resolution failed)
     * @param exception the exception that got thrown during handler execution
     * @return a corresponding {@code ModelAndView} to forward to,
     * or {@code null} for default processing in the resolution chain
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        this.HANDLER_HOLDER.set(handler);
        if (this.usingException) {
            this.EXCEPTION_HOLDER.set(exception);
        }
        // when log enable print request information
        if (this.httpRequestLogConfigProperties.isEnable()) {
            HttpRequestReporter httpRequestReporter = HttpRequestReporter.getInstance();
            httpRequestReporter.setRequest(request);
            httpRequestReporter.setException(exception);
            if (ValidateKit.isNotNull(handler)) {
                httpRequestReporter.setHandler((HandlerMethod) handler);
            }
            httpRequestReporter.report();

            // print error information
            StringBuilder errorBuilder = new StringBuilder();
            log.error(errorBuilder.append(Constants.REQUEST_REPORT_HEADER).toString());
        }
        return super.resolveException(request, response, handler, exception);
    }

    /**
     * Returns current handler execution 's instance, may be <tt>null</tt>.
     *
     * if "usingException" is <tt>false</tt> will return <tt>null</tt>.
     *
     * @return current handler execution 's exception instance
     */
    protected Exception getHandlerExecutionException() {
        if (!this.usingException) {
            throw new IllegalStateException("\"usingException\" state is Illegal, required true state.");
        }
        return this.EXCEPTION_HOLDER.getRemove();
    }

    /**
     * Current Request's status
     *
     * @see @{@linkplain org.springframework.http.HttpStatus}
     * @return response status
     */
    protected Integer getStatus(Map<String, Object> errorAttributes) {
        return ObjectKit.toInteger(errorAttributes.get("status"));
    }

    /**
     * @return Current Error
     */
    protected String getError(Map<String, Object> errorAttributes) {
        return errorAttributes.get("error").toString();
    }

    /**
     * @return Current Error Message
     */
    @Deprecated
    protected String getMessage(Map<String, Object> errorAttributes) {
        return errorAttributes.get("message").toString();
    }

    /**
     * @return <tt>true</tt> if response status is "OK" (200)
     */
    protected boolean responseOK(Map<String, Object> errorAttributes) {
        return HttpStatus.OK.value() == this.getStatus(errorAttributes);
    }
}