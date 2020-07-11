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

package org.openingo.spring.http.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openingo.jdkits.SystemClockKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * LogAspect
 *
 * @author Qicz
 */
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    final ThreadLocal<Long> httpRequestTimer = new ThreadLocal<>();

    private Long getProcessingTime() {
        try {
            return SystemClockKit.now() - this.httpRequestTimer.get();
        } finally {
            this.httpRequestTimer.remove();
        }
    }

    /**
     * this execution statement will dynamic update
     */
    @Pointcut("execution(public * org.oo..controller.*.*(..))")
    public void log() {
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return null;
//        this.httpRequestTimer.set(SystemClockKit.now());
//        Object proceed = point.proceed();
//        long processingTime = this.getProcessingTime();
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        HttpRequestReporter httpRequestReporter = HttpRequestReporter.getInstance(point);
//        // current target
//        httpRequestReporter.setTarget(point.getTarget());
//        // current request processing time
//        httpRequestReporter.setProcessingTime(processingTime);
//        ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
//        httpRequestReporter.setRequest(serverHttpRequest);
//        // body
//        Object body = null;
//        if (ValidateKit.isNotNull(request.getContentType())) {
//            body = "<File>";
//            try {
//                body = this.converter.read(Object.class, serverHttpRequest);
//                if (body instanceof Map) {
//                    body = JacksonKit.toJson(body);
//                }
//            } catch (Exception e) {
//                log.error(e.toString());
//            }
//        }
//        httpRequestReporter.setBody(body);
//
//        // response data
//        // ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
//        // httpRequestReporter.setResponse(servletServerHttpResponse);
//        Object data = HttpDataKit.getData();
//        if (ValidateKit.isNotNull(data)) {
//            httpRequestReporter.setResponseData(JacksonKit.toJson(data));
//        }
//        // fire report
//        httpRequestReporter.report();
//        return proceed;
    }
}
