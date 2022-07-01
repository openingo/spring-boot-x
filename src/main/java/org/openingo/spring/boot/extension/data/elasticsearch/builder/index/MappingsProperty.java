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

package org.openingo.spring.boot.extension.data.elasticsearch.builder.index;

import java.util.HashMap;

/**
 * MappingsProperty
 *
 * @author Qicz
 */
public final class MappingsProperty extends HashMap<String, Object> {

    private String name;

    private MappingsProperty() {}

    public static MappingsProperty me() {
        return new MappingsProperty().keyword();
    }

    public static MappingsProperty me(String name) {
        return MappingsProperty.me().name(name);
    }

    public MappingsProperty name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return this.name;
    }

    public MappingsProperty type(String type) {
        return this.add("type", type);
    }

    public MappingsProperty textType() {
        return this.type("text");
    }

    public MappingsProperty analyzer(String analyzer) {
        return this.add("analyzer", analyzer);
    }

    public MappingsProperty searchAnalyzer(String searchAnalyzer) {
        return this.add("search_analyzer", searchAnalyzer);
    }

    public MappingsProperty format(String format) {
        return this.add("format", format);
    }

    public MappingsProperty store(boolean store) {
        return this.add("store", store);
    }

    public MappingsProperty fielddata(boolean fielddata) {
        return this.add("fielddata", fielddata);
    }

    public MappingsProperty normalizer(String normalizer) {
        return this.add("normalizer", normalizer);
    }

    public MappingsProperty keyword() {
        return this.keyword(256);
    }

    public MappingsProperty keyword0() {
        return this.keyword(0);
    }

    public MappingsProperty keyword(int ignoreAbove) {
        MappingsProperty keyword = new MappingsProperty().type("keyword");
        if (ignoreAbove != 0) {
            keyword.add("ignore_above", ignoreAbove);
        }
        return this.add("fields", new MappingsProperty().add("keyword", keyword));
    }

    public MappingsProperty add(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
