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

import org.openingo.jdkits.ValidateKit;
import org.openingo.jdkits.http.RespData;
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

        Map<String, Object> serviceErrorAttributes = new HashMap<>();
        Object code = this.getStatus(errorAttributes);
        // deduce code type by RespData.Config.SUCCESS_SC
        if (RespData.Config.SUCCESS_SC instanceof String) {
            code = code.toString();
        }
        Object message = this.getError(errorAttributes);
        if (!this.responseOK(errorAttributes)) {
            Exception exception = this.getHandlerExecutionException();
            if (ValidateKit.isNotNull(exception)) {
                message = exception.getMessage();
                if (exception instanceof ServiceException) {
                    code = ((ServiceException) exception).getExceptionCode();
                }
            }
        }
        serviceErrorAttributes.put(RespData.Config.SC_KEY, code);
        serviceErrorAttributes.put(RespData.Config.SM_KEY, message);
        this.decorateErrorAttributes(errorAttributes, serviceErrorAttributes);
        return serviceErrorAttributes;
    }

    /**
     * Decorate error attributes, add extension attributes etc.
     * @param errorAttributes error attributes
     * @param serviceErrorAttributes service error attributes
     */
    public abstract void decorateErrorAttributes(Map<String, Object> errorAttributes,
                                                 Map<String, Object> serviceErrorAttributes);
}
