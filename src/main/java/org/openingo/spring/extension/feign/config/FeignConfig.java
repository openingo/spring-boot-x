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

package org.openingo.spring.extension.feign.config;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.collection.ListKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.extension.feign.hystrix.FeignHystrixConcurrencyStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FeignConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnClass(RequestInterceptor.class)
@Slf4j
public class FeignConfig {

    /**
     * put a RequestInterceptor that cover request headers
     * @return a RequestInterceptor
     */
    @Bean
    public RequestInterceptor requestHeadersCoverInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (ValidateKit.isNull(attrs)) {
                return;
            }
            HttpServletRequest request = attrs.getRequest();
            if (ValidateKit.isNull(request)) {
                return;
            }
            Enumeration<String> requestHeaderNames = request.getHeaderNames();
            if (ValidateKit.isNull(requestHeaderNames)) {
                return;
            }
            Map<String, Collection<String>> requestTemplateHeaders = requestTemplate.headers();
            // convert to lower headers
            List<String> lowerCaseRequestTemplateHeaders = requestTemplateHeaders.keySet().stream().map(String::toLowerCase).distinct().collect(Collectors.toList());
            while (requestHeaderNames.hasMoreElements()) {
                String name = requestHeaderNames.nextElement();
                String value = request.getHeader(name);
                Collection<String> values = requestTemplateHeaders.get(name);
                // convert to writeable
                values = new ArrayList<>(values);
                if (!lowerCaseRequestTemplateHeaders.contains(name.toLowerCase())) {
                    // None of the same names exist
                    // first time create new collection, the second time use the created
                    values = Optional.ofNullable(values).orElse(ListKit.emptyArrayList());
                    values.add(value);
                    requestTemplateHeaders.put(name, values);
                } else {
                    values.add(value);
                }
            }
            // clear old
            requestTemplate.headers(null);
            // put new
            requestTemplate.headers(requestTemplateHeaders);
        };
    }

    /**
     * rewrite hystrix strategy for concurrency
     */
    @Bean
    @ConditionalOnClass(HystrixConcurrencyStrategy.class)
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }
}
