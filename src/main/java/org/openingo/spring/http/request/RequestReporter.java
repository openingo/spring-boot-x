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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.openingo.jdkits.IPKit;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * HttpRequest Reporter
 *
 * @author Qicz
 */
@Data
@Slf4j
public final class RequestReporter {

	private ServletServerHttpRequest request;

	// current request processing time
	private Long processingTime;

	// current request bodyData data
	private Object bodyData;

	// response data
	private Object responseData;

	// current proceeding join point
	private ProceedingJoinPoint point;

	private RequestReporter(){}

	public static RequestReporter getInstance() {
		return new RequestReporter();
	}
	
	private static int maxOutputLengthOfParaValue = 512;

	/**
	 * Current Request report
	 */
	public void report() {
		HttpServletRequest servletRequest = this.request.getServletRequest();
		StringBuilder reportInfoBuilder = new StringBuilder(Constants.REQUEST_REPORT_HEADER);
		reportInfoBuilder.append("Client IP  : ").append(IPKit.getRequestIP(servletRequest)).append(" ").append("\n");
		reportInfoBuilder.append("Request Time  : ").append(LocalDateTime.now()).append(" ").append("\n");
		Class<?> target = this.point.getTarget().getClass();
		reportInfoBuilder.append("Controller  : ").append(target.getName()).append(".(").append(target.getSimpleName()).append(".java:1)").append("\n");
		reportInfoBuilder.append("URI  : ").append(this.request.getURI()).append(" ").append("\n");
		reportInfoBuilder.append("Handler(Action)  : ").append(this.point.getSignature().getName()).append("\n");
		reportInfoBuilder.append("Method  : ").append(this.request.getMethod()).append("\n");
		reportInfoBuilder.append("Processing Time  : ").append(this.processingTime/1000.0).append("s\n");

		// print all headers
		reportInfoBuilder.append("Header(s)  : ").append(this.request.getHeaders()).append("\n");

		// print all bodyData params
		if (ValidateKit.isNotNull(this.bodyData)) {
			reportInfoBuilder.append("Body  : ").append(this.bodyData).append("\n");
		}

		String urlQuery = servletRequest.getQueryString();
		if (ValidateKit.isNotNull(urlQuery)) {
			reportInfoBuilder.append("UrlQuery  : ").append(urlQuery).append("\n");
		}

		// print all parameters
		Enumeration<String> e = servletRequest.getParameterNames();
		if (e.hasMoreElements()) {
			reportInfoBuilder.append("Parameter(s)  : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = servletRequest.getParameterValues(name);
				if (values.length == 1) {
					reportInfoBuilder.append(name).append("=");
					if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
						reportInfoBuilder.append(values[0], 0, maxOutputLengthOfParaValue).append("...");
					} else {
						reportInfoBuilder.append(values[0]);
					}
				} else {
					reportInfoBuilder.append(name).append("[]={");
					for (int i=0; i < values.length; i++) {
						if (i > 0) {
							reportInfoBuilder.append(",");
						}
						reportInfoBuilder.append(values[i]);
					}
					reportInfoBuilder.append("}");
				}
				reportInfoBuilder.append(", ");
			}
			reportInfoBuilder.append("\n");
		}

		// response data
		if (ValidateKit.isNotNull(this.responseData)) {
			reportInfoBuilder.append("Response  : ").append(this.responseData).append("\n");
		}

		reportInfoBuilder.append("----------------------------------------------------------------\n");
		//PrintStream out = System.out;
		//out.println(AnsiOutput.toString(AnsiColor.GREEN, Constants.SPRING_APPLICATION_X));
		if (log.isInfoEnabled() || log.isDebugEnabled()) {
			log.info(reportInfoBuilder.toString());
		} else {
			System.out.println(reportInfoBuilder);
		}
	}
}