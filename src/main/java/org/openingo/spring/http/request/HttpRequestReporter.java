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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.openingo.jdkits.json.JacksonKit;
import org.openingo.jdkits.reflect.ClassKit;
import org.openingo.jdkits.sys.IPKit;
import org.openingo.jdkits.validate.ValidateKit;
import org.openingo.spring.constants.Constants;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
@Data
@Slf4j
public final class HttpRequestReporter {

	private HttpServletRequest request;

	// current request processing time
	private float processingTime = 0.0f;

	// response data
	private Object responseData = null;

	// current proceeding join point
	private ProceedingJoinPoint point;

	// action handler
	private HandlerMethod handler;

	// exception
	private Exception exception;

	private HttpRequestReporter(){}

	public static HttpRequestReporter getInstance() {
		return new HttpRequestReporter();
	}
	
	private static int maxOutputLengthOfParaValue = 512;

	/**
	 * Deduce current target class
	 * @return current target class
	 */
	private Class<?> deduceTargetClass() {
		Class<?> targetClass = null;
		if (ValidateKit.isNotNull(this.point)) {
			targetClass = this.point.getTarget().getClass();
		}
		if (ValidateKit.isNotNull(this.handler)) {
			targetClass = this.handler.getBean().getClass();
		}
		return ClassKit.getUserClass(targetClass);
	}

	/**
	 * Deduce current action name
	 * @return current action name
	 */
	private String deduceActionName() {
		if (ValidateKit.isNotNull(this.point)) {
			return this.point.getSignature().getName();
		}

		if (ValidateKit.isNotNull(this.handler)) {
			return this.handler.getMethod().getName();
		}
		return null;
	}

	public Object getBody() {
		ProceedingJoinPoint proceedingJoinPoint = ProceedingJoinPointHolder.getProceedingJoinPoint();
		if (ValidateKit.isNull(proceedingJoinPoint)) {
			return null;
		}
		Object[] joinPointArgs = proceedingJoinPoint.getArgs();
		Stream<?> stream = ValidateKit.isNull(joinPointArgs) ? Stream.empty() : Arrays.asList(joinPointArgs).stream();
		List<Object> bodyParams = stream
				.filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
				.collect(Collectors.toList());
		if (bodyParams.size() > 0) {
			Object bodyParam = bodyParams.get(0);
			if (!(bodyParam instanceof File
					|| bodyParam instanceof MultipartFile)) {
				return bodyParam;
			}
		}
		return null;
	}

	/**
	 * Current Request report
	 */
	@SneakyThrows
	public void report() {
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(this.request);
		StringBuilder reportInfoBuilder = new StringBuilder(Constants.REQUEST_REPORT_HEADER);
		reportInfoBuilder.append("Client IP  : ").append(IPKit.getRequestIP(this.request)).append(" ").append("\n");
		reportInfoBuilder.append("Request Time  : ").append(LocalDateTime.now()).append(" ").append("\n");
		Class<?> target = this.deduceTargetClass();
		if (ValidateKit.isNotNull(target)) {
			reportInfoBuilder.append("Controller  : ").append(target.getName()).append(".(").append(target.getSimpleName()).append(".java:1)").append("\n");
		}
		reportInfoBuilder.append("URI  : ").append(serverHttpRequest.getURI()).append(" ").append("\n");
		String actionName = this.deduceActionName();
		if (ValidateKit.isNotNull(actionName)) {
			reportInfoBuilder.append("Handler(Action)  : ").append(actionName).append("\n");
		}
		reportInfoBuilder.append("Method  : ").append(serverHttpRequest.getMethod()).append("\n");
		reportInfoBuilder.append("Processing Time  : ").append(this.processingTime).append("s\n");

		// print all headers
		reportInfoBuilder.append("Header(s)  : ").append(serverHttpRequest.getHeaders()).append("\n");

		// print all bodyData params
		// bodyData data
		Object body = this.getBody();
		if (ValidateKit.isNotNull(body)) {
			body = JacksonKit.toJson(body);
		} else {
			body = "<File>";
		}
		reportInfoBuilder.append("Body  : ").append(body).append("\n");

		String urlQuery = this.request.getQueryString();
		if (ValidateKit.isNotNull(urlQuery)) {
			reportInfoBuilder.append("UrlQuery  : ").append(urlQuery).append("\n");
		}

		// print all parameters
		Enumeration<String> e = this.request.getParameterNames();
		if (e.hasMoreElements()) {
			reportInfoBuilder.append("Parameter(s)  : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = this.request.getParameterValues(name);
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
			reportInfoBuilder.append("Response  : ").append(JacksonKit.toJson(this.responseData)).append("\n");
		}

		// exception
		if (ValidateKit.isNotNull(this.exception)) {
			reportInfoBuilder.append("Exception  : ").append(this.exception.toString()).append("\n");
			//reportInfoBuilder.append("Exception Message  : ").append(this.exception.getMessage()).append("\n");
		}

		reportInfoBuilder.append("----------------------------------------------------------------\n");
		this.report(reportInfoBuilder.toString());
	}

	/**
	 * Report information
	 * @param info
	 */
	public void report(String info) {
		//PrintStream out = System.out;
		//out.println(AnsiOutput.toString(AnsiColor.GREEN, Constants.SPRING_APPLICATION_X));
		if (log.isInfoEnabled() || log.isDebugEnabled()) {
			log.info(info);
		} else {
			System.out.println(info);
		}
	}
}