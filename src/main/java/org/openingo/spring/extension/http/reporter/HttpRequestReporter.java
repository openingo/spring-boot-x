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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openingo.jdkits.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.method.HandlerMethod;

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
public final class HttpRequestReporter {

	private HandlerMethod handler;
	private ServletServerHttpRequest request;
	private Long processingTime;
	private Object body;

	private HttpRequestReporter(){}

	public static HttpRequestReporter getInstance() {
		return new HttpRequestReporter();
	}
	
	private static int maxOutputLengthOfParaValue = 512;

	/**
	 * Current Request report
	 */
	public void report() {
		StringBuilder reporterMaker = new StringBuilder();
		reporterMaker.append("\n--------------------------------------------------------------------------------\n");
		reporterMaker.append(Constants.SPRING_APPLICATION_X).append(" request report \n");
		//sb.append("IP  : ").append(IPKit.getRequestIP(request)).append(" ").append("\n");
		Class<?> bean = this.handler.getBean().getClass();
		reporterMaker.append("Controller  : ").append(bean.getName()).append(".(").append(bean.getSimpleName()).append(".java:1)").append("\n");
		reporterMaker.append("URI  : ").append(this.request.getURI()).append(" ").append("\n");
		reporterMaker.append("Handler(Action)  : ").append(this.handler.getMethod().getName()).append("\n");
		reporterMaker.append("Method  : ").append(this.request.getMethod()).append("\n");
		reporterMaker.append("Request Time  : ").append(LocalDateTime.now()).append(" ").append("\n");
		reporterMaker.append("Processing Time  : ").append(this.processingTime/1000.0).append("s\n");

		// print all headers
		reporterMaker.append("Header(s)  : ").append(this.request.getHeaders()).append("\n");

		// print all body params
		reporterMaker.append("Body  : ");
		if (ValidateKit.isNotNull(this.body)) {
			reporterMaker.append(this.body).append("\n");
		} else {
			reporterMaker.append("<File>").append("\n");
		}

		HttpServletRequest servletRequest = this.request.getServletRequest();
		String urlQuery = servletRequest.getQueryString();
		if (ValidateKit.isNotNull(urlQuery)) {
			reporterMaker.append("UrlQuery  : ").append(urlQuery).append("\n");
		}

		// print all parameters
		Enumeration<String> e = servletRequest.getParameterNames();
		if (e.hasMoreElements()) {
			reporterMaker.append("Parameter  : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = servletRequest.getParameterValues(name);
				if (values.length == 1) {
					reporterMaker.append(name).append("=");
					if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
						reporterMaker.append(values[0], 0, maxOutputLengthOfParaValue).append("...");
					} else {
						reporterMaker.append(values[0]);
					}
				} else {
					reporterMaker.append(name).append("[]={");
					for (int i=0; i < values.length; i++) {
						if (i > 0)
							reporterMaker.append(",");
						reporterMaker.append(values[i]);
					}
					reporterMaker.append("}");
				}
				reporterMaker.append(", ");
			}
			reporterMaker.append("\n");
		}

		reporterMaker.append("--------------------------------------------------------------------------------\n");
		//PrintStream out = System.out;
		//out.println(AnsiOutput.toString(AnsiColor.GREEN, Constants.SPRING_APPLICATION_X));
		if (log.isInfoEnabled() || log.isDebugEnabled()) {
			log.info(reporterMaker.toString());
		} else {
			System.out.println(reporterMaker);
		}
	}
}