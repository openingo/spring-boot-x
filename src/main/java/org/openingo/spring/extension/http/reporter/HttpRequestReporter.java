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

package org.openingo.spring.extension.http.reporter;

import org.openingo.jdkits.IPKit;
import org.openingo.jdkits.JacksonKit;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.boot.SpringApplicationX;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HttpRequest Reporter
 *
 * @author Qicz
 */
public final class HttpRequestReporter {

	private HttpRequestReporter(){}
	
	private static int maxOutputLengthOfParaValue = 512;

	public static final StringBuilder report(HandlerMethod handler, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n--------------------------------------------------------------------------------\n");
		sb.append("SpringApplicationX - (" + SpringApplicationX.springBootVersionX + ") request report \n");
		sb.append("IP  : ").append(IPKit.getRequestIP(request)).append(" ").append("\n");
		Class<?> cc = handler.getBean().getClass();
		sb.append("Class  : ").append(cc.getName()).append(".(").append(cc.getSimpleName()).append(".java:1)").append("\n");
		sb.append("Url  : ").append(request.getRequestURL()).append(" ").append("\n");
		sb.append("Logic  : ").append(handler.getMethod().getName()).append("\n");
		sb.append("Datetime  : ").append(LocalDateTime.now()).append(" ").append("\n");
		sb.append("Method  : ").append(request.getMethod()).append("\n");

		// print all headers
		StringBuilder headerBuf = new StringBuilder();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			if (headerBuf.length() > 0) {
				headerBuf.append("\n");
			}
			headerBuf.append("   ").append(key).append("=").append(value);
		}
		sb.append("Header(s)  : \n").append(headerBuf).append("\n");

		// TODO print all body params
		Object[] pointArgs = new Object[]{};
		Stream<?> stream = ValidateKit.isEmpty(pointArgs) ? Stream.empty() : Arrays.asList(pointArgs).stream();
		List<Object> bodyParams = stream
				.filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
				.collect(Collectors.toList());
		if (ValidateKit.isNotNull(bodyParams)) {
			Object bodyData = bodyParams.get(0);
			if (bodyData instanceof File || bodyData instanceof MultipartFile) {
				sb.append("bodyParams  : ").append("<File>").append("\n");
			} else {
				sb.append("bodyParams[JSON]  : ").append(JacksonKit.toJson(bodyData)).append("\n");
			}
		}

		String urlParams = request.getQueryString();
		if (urlParams != null) {
			sb.append("UrlParams  : ").append(urlParams).append("\n");
		}

		// print all parameters
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("Parameter  : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=");
					if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
						sb.append(values[0], 0, maxOutputLengthOfParaValue).append("...");
					} else {
						sb.append(values[0]);
					}
				}
				else {
					sb.append(name).append("[]={");
					for (int i=0; i<values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
			sb.append("\n");
		}

		sb.append("--------------------------------------------------------------------------------\n");
		return sb;
	}
}