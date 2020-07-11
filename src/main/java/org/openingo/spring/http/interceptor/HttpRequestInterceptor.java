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

package org.openingo.spring.http.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.JacksonKit;
import org.openingo.jdkits.SystemClockKit;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.http.kit.HttpDataKit;
import org.openingo.spring.http.reporter.HttpRequestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * HttpRequestInterceptor
 *
 * @author Qicz
 */
@Slf4j
public class HttpRequestInterceptor implements HandlerInterceptor {

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    final ThreadLocal<Long> httpRequestTimer = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.httpRequestTimer.set(SystemClockKit.now());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // nothing
    }

    private Long getProcessingTime() {
        try {
            return SystemClockKit.now() - this.httpRequestTimer.get();
        } finally {
            this.httpRequestTimer.remove();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long processingTime = this.getProcessingTime();
        if (handler instanceof HandlerMethod/*
                    && (((HandlerMethod) handler).getBean().getClass().getPackage().getName().contains(SpringApplicationX.applicationPackage))*/) {
            HttpRequestReporter httpRequestReporter = HttpRequestReporter.getInstance();
            // current handler
            httpRequestReporter.setHandler(((HandlerMethod) handler));
            // current request processing time
            httpRequestReporter.setProcessingTime(processingTime);
            ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
            httpRequestReporter.setRequest(serverHttpRequest);
            // body
            Object body = null;
            if (ValidateKit.isNotNull(request.getContentType())) {
                body = "<File>";
                try {
                    body = this.converter.read(Object.class, serverHttpRequest);
                    if (body instanceof Map) {
                        body = JacksonKit.toJson(body);
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
            httpRequestReporter.setBody(body);

            // response data
            // ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
            // httpRequestReporter.setResponse(servletServerHttpResponse);
            Object data = HttpDataKit.getData();
            if (ValidateKit.isNotNull(data)) {
                httpRequestReporter.setResponseData(JacksonKit.toJson(data));
            }
            // fire report
            httpRequestReporter.report();
        }
    }
}
