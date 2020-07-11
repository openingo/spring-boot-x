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

import com.fasterxml.jackson.databind.util.ClassUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.constants.Constants;
import org.openingo.spring.constants.PropertiesConstants;
import org.openingo.spring.http.advice.ExceptionAdvice;
import org.openingo.spring.http.aop.DynamicMethodMatcherPointcutX;
import org.openingo.spring.http.handler.ExceptionHandler;
import org.openingo.spring.http.handler.ReturnValueHandler;
import org.openingo.spring.http.interceptor.HttpRequestInterceptor;
import org.openingo.spring.http.log.LogAspect;
import org.openingo.spring.kit.ExtensionKit;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import sun.rmi.runtime.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpConfig
 *
 * @author Qicz
 */
@Configuration
@ConditionalOnProperty(
        prefix = PropertiesConstants.HTTP_CONFIG_PROPERTIES_PREFIX,
        name = PropertiesConstants.ENABLE,
        havingValue = Constants.TRUE,
        matchIfMissing = true // default enable
)
@ConditionalOnClass(WebMvcConfigurer.class)
public class HttpConfig  {

    //@Bean
    public WebMvcConfigurerX webMvcConfigurerX() {
        return new WebMvcConfigurerX();
    }

    @Bean
    public HttpRequestInterceptor httpRequestInterceptor() {
        return new HttpRequestInterceptor();
    }

    @Bean
    public LogAspect logAspect()  {
        LogAspect instance = null;
        try {
            Class<LogAspect> logAspectClass = LogAspect.class;
            ClassPool aDefault = ClassPool.getDefault();
            CtClass ctClass = aDefault.getCtClass(logAspectClass.getName());
            CtClass cc = aDefault.makeClass(logAspectClass.getName()+"X", ctClass);
            ClassFile classFile = cc.getClassFile();

            ConstPool constpool = classFile.getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            Annotation annot = new Annotation(Aspect.class.getName(), constpool);
            attr.addAnnotation(annot);
            classFile.addAttribute(attr);
            logAspectClass = cc.toClass();

            Method log = logAspectClass.getMethod("log", null);
            Pointcut annotation = log.getAnnotation(Pointcut.class);
            System.out.println("before===🔥"+annotation.value());
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            String value = SpringApplicationX.applicationPackage;
            value = String.format("execution(public * %s..controller.*.*(..))", value);
            ExtensionKit.annotionValueReplace(invocationHandler, "value", value);

            logAspectClass = (Class<LogAspect>)this.getClass().getClassLoader().loadClass(logAspectClass.getName());

            instance = ClassUtil.createInstance(logAspectClass, true);

            Method log1 = instance.getClass().getMethod("log", null);
            Pointcut annotation1 = log1.getAnnotation(Pointcut.class);
            System.out.println("after===🔥"+annotation1.value());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return instance;
    }

    @Configuration
    public static class ReturnValueConfig implements InitializingBean {

        @Autowired
        RequestMappingHandlerAdapter requestMappingHandlerAdapter;

        /**
         * Invoked by the containing {@code BeanFactory} after it has set all bean properties
         * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
         * <p>This method allows the bean instance to perform validation of its overall
         * configuration and final initialization when all bean properties have been set.
         * @throws Exception in the event of misconfiguration (such as failure to set an
         * essential property) or if initialization fails for any other reason
         */
        @Override
        public void afterPropertiesSet() throws Exception {
            List<HandlerMethodReturnValueHandler> unmodifiableList = this.requestMappingHandlerAdapter.getReturnValueHandlers();
            List<HandlerMethodReturnValueHandler> list = new ArrayList<>(unmodifiableList.size());
            for (HandlerMethodReturnValueHandler returnValueHandler : unmodifiableList) {
                if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                    list.add(new ReturnValueHandler(returnValueHandler));
                } else {
                    list.add(returnValueHandler);
                }
            }
            this.requestMappingHandlerAdapter.setReturnValueHandlers(list);
        }
    }
}
