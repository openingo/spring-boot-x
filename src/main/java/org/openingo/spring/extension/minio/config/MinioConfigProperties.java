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

package org.openingo.spring.extension.minio.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openingo.spring.config.ExtensionConfigProperties;
import org.openingo.spring.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinioConfigProperties
 *
 * @author Qicz
 * @since 2021/6/11 15:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = PropertiesConstants.MINIO_CONFIG_PROPERTIES_PREFIX)
public class MinioConfigProperties extends ExtensionConfigProperties {

	private String endpoint;

	/**
	 * aka userId
	 */
	private String accessKey;

	/**
	 * aka password
	 */
	private String secretKey;

	private String bucket;

	private String uploadPath;

	private Integer uploadExpireSeconds;

	private Integer previewExpireSeconds;

	/**
	 * the min size kib
	 */
	private Integer allowMinSize;

	/**
	 * the max size kib
	 */
	private Integer allowMaxSize;

	private String allowFileTypes;
}
