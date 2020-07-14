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

package org.openingo.spring.http.request;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openingo.jdkits.SystemClockKit;
import org.openingo.jdkits.ThreadLocalKit;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.request.RequestReporter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * RequestLogAspect
 *
 * @author Qicz
 */
@Aspect
@Slf4j
public class RequestLogAspect {

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    private final ThreadLocalKit<Long> processingTimeThreadLocal = new ThreadLocalKit<>();

    private void handlerStart() {
        this.processingTimeThreadLocal.set(SystemClockKit.now());
    }

    private float getProcessingSeconds() {
        long startTime = this.processingTimeThreadLocal.get();
        return (SystemClockKit.now() - startTime)/1000.0f;
    }

    @Pointcut("execution(public * *.*..controller..*.*(..))")
    public void log() {
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        this.handlerStart();
        Object proceed = point.proceed();
        float processingTime = this.getProcessingSeconds();
        RequestReporter requestReporter = RequestReporter.getInstance();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (ValidateKit.isNull(request)) {
            requestReporter.report(Constants.REQUEST_REPORT_HEADER + "Processing Time  : " + processingTime + "s\n");
            return proceed;
        }

        // current request processing time
        requestReporter.setProcessingTime(processingTime);
        requestReporter.setPoint(point);
        requestReporter.setConverter(this.converter);
        requestReporter.setRequest(request);
        requestReporter.setResponseData(proceed);

        // fire report
        requestReporter.report();
        return proceed;
    }
}
