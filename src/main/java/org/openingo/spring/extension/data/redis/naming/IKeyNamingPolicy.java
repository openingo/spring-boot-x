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

package org.openingo.spring.extension.data.redis.naming;

import org.openingo.jdkits.ListKit;
import org.openingo.jdkits.ValidateKit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IKeyNamingPolicy
 *
 * @author Qicz
 */
public interface IKeyNamingPolicy {

    /**
     * Get naming key
     * @param key
     * @return naming key
     */
    String getKeyName(String key);

    /**
     * Get naming keys
     * @param keys
     * @return naming keys
     */
    default String[] getKeyNames(String... keys) {
        if (ValidateKit.isNull(keys)) {
            return new String[]{};
        }
        String[] keyNames = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keyNames[i] = this.getKeyName(keys[i]);
        }
        return keyNames;
    }

    /**
     * Get naming keys
     * @param keys
     * @return naming keys
     */
    default List<String> getKeyNames(Collection<String> keys) {
        if (ValidateKit.isNull(keys)) {
            return ListKit.emptyArrayList();
        }
        return keys.stream().map(this::getKeyName).collect(Collectors.toList());
    }

    /**
     * Map keys naming
     * @param map
     * @return a new map, the map 's keys renamed.
     */
    default <V> Map<? extends String, ? extends V> mapKeyNaming(Map<? extends String, ? extends V> map) {
        Map<String, V> mapCp = new HashMap<>(map.size());
        map.keySet().forEach(k -> {
            mapCp.put(this.getKeyName(k), map.get(k));
        });
        return mapCp;
    }
}
