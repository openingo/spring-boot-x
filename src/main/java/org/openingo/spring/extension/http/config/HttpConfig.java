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

package org.openingo.spring.extension.http.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.constants.PropertiesConstants;
import org.openingo.spring.http.mvc.HttpWebMvcConfigurer;
import org.openingo.spring.http.request.HttpRequestLogAspect;
import org.openingo.spring.http.request.error.DefaultServiceErrorAttributes;
import org.openingo.spring.http.response.HttpResponseBodyAdvice;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * HttpConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
public class HttpConfig  {

    @Bean
    @ConditionalOnProperty(
            prefix = PropertiesConstants.HTTP_REQUEST_LOG_CONFIG_PROPERTIES_PREFIX,
            name = PropertiesConstants.ENABLE,
            havingValue = Constants.TRUE,
            matchIfMissing = true // default enable
    )
    public HttpRequestLogAspect httpRequestLogAspect() {
        return new HttpRequestLogAspect();
    }

    @Configuration
    @ConditionalOnProperty(
            prefix = PropertiesConstants.HTTP_REQUEST_ERROR_CONFIG_PROPERTIES_PREFIX,
            name = PropertiesConstants.ENABLE,
            havingValue = Constants.TRUE,
            matchIfMissing = true // default enable
    )
    static class HttpErrorConfig {

        /**
         * DefaultServiceErrorAttributes
         */
        @Bean
        @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
        public DefaultServiceErrorAttributes serviceDefaultErrorAttributes() {
            return new DefaultServiceErrorAttributes();
        }

        @Bean(name = "error")
        @ConditionalOnMissingBean(name = "error")
        public View defaultErrorView() {
            return new StaticErrorView();
        }

        static class StaticErrorView implements View {

            private static final Log logger = LogFactory.getLog(StaticErrorView.class);

            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
                if (response.isCommitted()) {
                    String message = getMessage(model);
                    logger.error(message);
                    return;
                }
                StringBuilder builder = new StringBuilder();
                if (response.getContentType() == null) {
                    response.setContentType(getContentType());
                }
                builder.append("<html><body><h1> :: SpringApplicationX :: Whitelabel Error Page</h1>");
                builder.append("<h3>Error Details:</h3>");
                model.forEach((k, v) -> builder.append("<div> ").append(k).append(" => \"").append(v).append("\" </div>"));
                builder.append("</body></html>");
                response.getWriter().append(builder.toString());
            }

            private String getMessage(Map<String, ?> model) {
                Object path = model.get("path");
                String message = "Cannot render error page for request [" + path + "]";
                Object mMessage = model.get("message");
                if (ValidateKit.isNotNull(mMessage)) {
                    message += " and exception [" + mMessage + "]";
                }
                message += " as the response has already been committed.";
                message += " As a result, the response may have the wrong status code.";
                return message;
            }

            @Override
            public String getContentType() {
                return MediaType.TEXT_HTML_VALUE;
            }
        }
    }

    @Bean
    @ConditionalOnProperty(
            prefix = PropertiesConstants.HTTP_REQUEST_CORS_CONFIG_PROPERTIES_PREFIX,
            name = PropertiesConstants.ENABLE,
            havingValue = Constants.TRUE
    )
    @ConditionalOnMissingBean
    public CorsFilter corsFilter(HttpConfigProperties.HttpRequestCorsConfigProperties corsConfigProperties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        String allowedOrigin = corsConfigProperties.getAllowedOrigin();
        String allowedHeader = corsConfigProperties.getAllowedHeader();
        String allowedMethod = corsConfigProperties.getAllowedMethod();
        if (corsConfigProperties.isAllowedAll()) {
            allowedOrigin = CorsConfiguration.ALL;
            allowedHeader = CorsConfiguration.ALL;
            allowedMethod = CorsConfiguration.ALL;
        }
        if (ValidateKit.isNotNull(allowedOrigin)) {
            corsConfiguration.addAllowedOrigin(allowedOrigin);
        }
        if (ValidateKit.isNotNull(allowedHeader)) {
            corsConfiguration.addAllowedHeader(allowedHeader);
        }
        if (ValidateKit.isNotNull(allowedMethod)) {
            corsConfiguration.addAllowedMethod(allowedMethod);
        }
        corsConfiguration.setAllowCredentials(corsConfigProperties.isAllowCredentials());
        String path = corsConfigProperties.getPath();
        if (ValidateKit.isNotNull(path)) {
            source.registerCorsConfiguration(path, corsConfiguration);
        }
        return new CorsFilter(source);
    }

    /**
     * the http mvn configure for some interceptors etc.
     */
    @Bean
    public HttpWebMvcConfigurer httpWebMvcConfigurer() {
        return new HttpWebMvcConfigurer();
    }

    @ControllerAdvice
    @Order(-100)
    static class HttpControllerAdvice extends HttpResponseBodyAdvice {

    }
}
