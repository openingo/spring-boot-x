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

package org.openingo.spring.kit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DiffPropertiesHelper
 *
 * @author Qicz
 */
public final class DiffPropertiesHelper {

    private static ThreadLocal<ConcurrentHashMap<String, Object>> DIFF_PROPERTIES_HOLDER = new ThreadLocal<>();

    public static void prepare() {
        if (null != get()) {
            return;
        }
        // default has 20 properties
        DIFF_PROPERTIES_HOLDER.set(new ConcurrentHashMap<>(20));
    }

    public static Map<String, Object> get() {
        return DIFF_PROPERTIES_HOLDER.get();
    }

    public static void clear() {
        DIFF_PROPERTIES_HOLDER.remove();
    }

    public static void addObjProperty(String objPropertyName) {
        Map<String, Object> data = get();
        if (null == data || data.containsKey(objPropertyName)) {
            return;
        }
        data.put(objPropertyName, new ConcurrentHashMap<>(10));
    }

    public static void put(String propertyName,
                           Object newValue,
                           Object oldValue) {
        Map<String, Object> data = get();
        if (null == data) {
            return;
        }
        if (null != newValue
                && !newValue.equals(oldValue)) {
            String[] property = propertyName.split("\\.");
            int length = property.length;
            if (length == 1) {
                data.put(propertyName, newValue);
            } else if (length == 2) {
                String tmpProperty = property[0];
                Map<String, Object> objProperty = null;
                if (data.containsKey(tmpProperty)
                        || data.containsKey(tmpProperty += "-ERROR-KEY")) {
                    objProperty = (Map<String, Object>)data.get(tmpProperty);
                } else {
                    objProperty = new ConcurrentHashMap<>(10);
                    data.put(tmpProperty, objProperty);
                }
                objProperty.put(property[1], newValue);
            }
        }
    }
}
