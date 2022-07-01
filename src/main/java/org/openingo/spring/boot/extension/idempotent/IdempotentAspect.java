/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
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

package org.openingo.spring.boot.extension.idempotent;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.openingo.jdkits.lang.StrKit;
import org.openingo.spring.boot.extension.distributedlock.lock.DistributedLock;
import org.openingo.spring.boot.extension.idempotent.annotation.Idempotent;
import org.openingo.spring.boot.extension.idempotent.annotation.IdempotentKey;
import org.openingo.spring.boot.extension.idempotent.store.IdempotentStore;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * IdempotentAspect
 *
 * @author Qicz
 * @since 2021/8/9 14:06
 */
@Aspect
@Slf4j
public class IdempotentAspect {

	private final IdempotentStore idempotentStore;

	public IdempotentAspect(IdempotentStore idempotentStore) {
		this.idempotentStore = idempotentStore;
	}

	@Pointcut("@annotation(org.openingo.spring.boot.extension.idempotent.annotation.Idempotent)")
	public void idempotentPointcut() {
	}

	@Around("idempotentPointcut()&&@annotation(idempotent)")
	public Object idempotentExecute(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method targetMethod = methodSignature.getMethod();
		Object[] args = joinPoint.getArgs();
		String idempotentKey = this.getIdempotentKey(idempotent, targetMethod, args);
		Assert.hasText(idempotentKey, "idempotent key is empty");
		StringBuilder idempotentKeyBuilder = new StringBuilder(targetMethod.getDeclaringClass().getName());
		idempotentKeyBuilder.append(".").append(targetMethod.getName());
		for (Class<?> paramType : targetMethod.getParameterTypes()) {
			idempotentKeyBuilder.append(KEY_SPACER).append(paramType.getName());
		}
		idempotentKey = idempotentKeyBuilder.append(KEY_SPACER).append(idempotentKey).toString().toLowerCase().trim();
		log.info("idempotent key {} args {}", idempotentKey, Arrays.toString(args));

		final String lockKey = getLockKey(idempotentKey);
		log.info("lock key {}", lockKey);
		final Lock lock = DistributedLock.newLock(lockKey);
		try {
			log.info("try get distribute lock for idempotent");
			Assert.isTrue(lock.tryLock(), "try lock error, wait a minutes");
			// find history data
			Object data = this.idempotentStore.getData(idempotentKey, targetMethod.getReturnType());
			if (Objects.nonNull(data)) {
				log.info("data from the history");
				return data;
			}
			data = joinPoint.proceed(args);
			// save data
			this.idempotentStore.saveData(idempotentKey, data, idempotent.expireMinutes());
			return data;
		} finally {
			log.info("distribute unlock for idempotent");
			lock.unlock();
		}
	}

	private String getLockKey(String idempotentKey) {
		return String.format("idempotent-locks:%s", idempotentKey);
	}

	private String getIdempotentKey(Idempotent idempotent, Method targetMethod, Object[] args) throws Exception {
		String key = "";
		// get fro el
		final String keyEl = idempotent.keyEl();
		if (StrKit.notBlank(keyEl)) {
			key = eval(keyEl, targetMethod, args);
		}

		if (StrKit.notBlank(key)) {
			return key;
		}

		// get from parameters
		Annotation[][] paramsAnnotations = targetMethod.getParameterAnnotations();
		for (int idx = 0; idx < paramsAnnotations.length; idx++) {
			boolean idempotentKeyExist = false;
			for (Annotation annotation : paramsAnnotations[idx]) {
				if (idempotentKeyExist = annotation instanceof IdempotentKey) {
					Object tempArg = args[idx];
					key = tempArg == null ? "" : tempArg.toString();
					break;
				}
			}
			if (idempotentKeyExist) {
				break;
			}
		}
		if (StrKit.notBlank(key)) {
			return key;
		}
		return key;
	}

	private static final String KEY_SPACER = "-";

	private static final ExpressionParser PARSER = new SpelExpressionParser();

	private static final LocalVariableTableParameterNameDiscoverer DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

	private static String eval(String el, Method targetMethod, Object[] args) {
		String[] parameterNames = DISCOVERER.getParameterNames(targetMethod);
		EvaluationContext context = new StandardEvaluationContext();
		if (Objects.nonNull(parameterNames)) {
			for (int len = 0; len < parameterNames.length; len++) {
				context.setVariable(parameterNames[len], args[len]);
			}
		}
		Expression expression = PARSER.parseExpression(el);
		return expression.getValue(context, String.class);
	}
}
