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

import lombok.SneakyThrows;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Collections;
import java.util.Map;

/**
 * the data 's different properteis finder
 *
 * @author Qicz
 */
public interface DiffPropertiesFinder<T> {

    /**
     * find data obj's different properties
     * @param objClass the obj class
     * @param objPropertyName the obj name
     * @param newValue current new value
     * @param oldValue old value
     */
    @SneakyThrows
    default void findObjDiffProperties(Class<?> objClass,
                                       String objPropertyName,
                                       Object newValue,
                                       Object oldValue) {
        if (null == objClass || null == newValue) {
            return;
        }
        // find different
        if (null == oldValue) {
            // prepare one mock data
            oldValue = objClass.newInstance();
        }
        // add one obj property
        DiffPropertiesHelper.addObjProperty(objPropertyName);
        BeanCopier.create(objClass, objClass, false)
                .copy(newValue, oldValue, null);
    }

    /**
     * find data different properties
     * @param clazz data class
     * @param newOne obj new one instance
     * @return the different data that is putted to Map.
     */
    default Map<String, Object> findDiffProperties(Class<T> clazz, T newOne) {
        if (null == clazz || null == newOne) {
            return Collections.emptyMap();
        }
        try {
            DiffPropertiesHelper.prepare();
            BeanCopier.create(clazz, clazz, false)
                    .copy(newOne, this, null);
            Map<String, Object> ret = DiffPropertiesHelper.get();
            if (null == ret) {
                ret = Collections.emptyMap();
            }
            return ret;
        } finally {
            DiffPropertiesHelper.clear();
        }
    }

    /**
     * put the different property
     * @param propertyName current property name
     * @param newValue current property new value
     * @param oldValue current property old value
     */
    default void putDiffProperty(String propertyName,
                                 Object newValue,
                                 Object oldValue) {
        if (null == propertyName
                || propertyName.isEmpty()
                || null == newValue) {
            return;
        }
        DiffPropertiesHelper.put(propertyName, newValue, oldValue);
    }
}
