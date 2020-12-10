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

package org.openingo.x.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;
import org.openingo.spring.boot.SpringApplicationX;
import org.openingo.spring.extension.data.elasticsearch.builder.index.MappingsProperties;
import org.openingo.spring.extension.data.elasticsearch.builder.index.MappingsProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * LocalDemo
 *
 * @author Qicz
 */
public class LocalDemo {

    @Test
    public void testVersion() {
        String springBootVersionX = SpringApplicationX.springBootVersionX;
        System.out.println(springBootVersionX);

        System.out.println(SpringApplicationX.springBootVersion);

    }

    @Test
    public void editAnno() {
        Class aClass = DyClass.class;
        Method dy1 = null;
        try {
            dy1 = aClass.getDeclaredMethod("dy", null);
            Pointcut annotation1 = dy1.getAnnotation(Pointcut.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation1);

            Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            memberValues.setAccessible(true);
            // 获取 memberValues
            Map memberValuesMap = (Map) memberValues.get(invocationHandler);
            // 修改 value 属性值
            memberValuesMap.put("value", "ddd");
            // 获取 foo 的 value 属性值
            String value = annotation1.value();
            System.out.println(value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testPJson() {
        String CREATE_INDEX = "{\n" +
                "    \"properties\": {\n" +
                "      \"id\":{\n" +
                "        \"type\":\"integer\"\n" +
                "      },\n" +
                "      \"userId\":{\n" +
                "        \"type\":\"integer\"\n" +
                "      },\n" +
                "      \"name\":{\n" +
                "        \"type\":\"text\",\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"url\":{\n" +
                "        \"type\":\"text\",\n" +
                "        \"index\": true,\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      }\n" +
                "    }\n" +
                "  }";

        System.out.println(CREATE_INDEX);
    }

    @Test
    public void abc() throws JsonProcessingException {
        MappingsProperties me = MappingsProperties.me();
        me.add("addr", MappingsProperty.me().type("text").keyword(256));
        me.add("name", MappingsProperty.me().type("text").keyword(256));
        System.out.println(me.toJson());
    }
}
