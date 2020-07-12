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

package org.openingo.spring.http.error;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.http.kit.HttpThreadLocalDataKit;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * WebErrorAttributesX
 *
 * @author Qicz
 */
@Slf4j
public class WebErrorAttributesX extends DefaultErrorAttributes {

    private Object handler;
    private Exception ex;

    /**
     * Returns a {@link Map} of the error attributes. The map can be used as the model of
     * an error page {@link ModelAndView}, or returned as a {@link ResponseBody}.
     * @param webRequest the source request
     * @param includeStackTrace if stack trace elements should be included
     * @return a map of error attributes
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        if (ValidateKit.isNotNull(this.handler)) {
            errorAttributes.put("handler", this.handler.toString());
        }
        if (ValidateKit.isNotNull(this.ex)) {
            errorAttributes.put("exception", this.ex.toString());
            HttpThreadLocalDataKit.putData(this.ex);
        }
        this.reset();
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
     * @param ex       the exception that got thrown during handler execution
     * @return a corresponding {@code ModelAndView} to forward to,
     * or {@code null} for default processing in the resolution chain
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        this.handler = handler;
        this.ex = ex;
        StringBuilder errorBuilder = new StringBuilder();
        log.error(errorBuilder.append(Constants.REQUEST_REPORT_HEADER).toString());
        return super.resolveException(request, response, handler, ex);
    }

    private void reset() {
        this.handler = null;
        this.ex = null;
    }

    /**
     * @return current handler execution 's exceptions, may be <tt>null</tt>
     */
    protected Exception getHandlerExecutionException() {
        return HttpThreadLocalDataKit.getData();
    }
}
