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

package org.openingo.spring.constants;

/**
 * PropertiesConstants
 *
 * @author Qicz
 */
public final class PropertiesConstants {

    private PropertiesConstants(){}

    // redis config properties prefix
    public static final String REDIS_CONFIG_PROPERTIES_PREFIX = "openingo.redis";

    // http configs
    private static final String HTTP_REQUEST_CONFIG_PROPERTIES_PREFIX = "openingo.http.request.";
    // http request log config properties prefix
    public static final String HTTP_REQUEST_LOG_CONFIG_PROPERTIES_PREFIX = HTTP_REQUEST_CONFIG_PROPERTIES_PREFIX + "log";
    // http request error config properties prefix
    public static final String HTTP_REQUEST_ERROR_CONFIG_PROPERTIES_PREFIX = HTTP_REQUEST_CONFIG_PROPERTIES_PREFIX + "error";
    // http request cors config properties prefix
    public static final String HTTP_REQUEST_CORS_CONFIG_PROPERTIES_PREFIX = HTTP_REQUEST_CONFIG_PROPERTIES_PREFIX + "cors";

    // enable
    public static final String ENABLE = "enable";
}
