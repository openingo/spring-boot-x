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

package org.openingo.spring.boot.extension.minio.config;

import io.minio.MinioClient;
import org.openingo.spring.boot.constants.Constants;
import org.openingo.spring.boot.constants.PropertiesConstants;
import org.openingo.spring.boot.extension.minio.MinioClientX;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinioConfig
 *
 * @author Qicz
 * @since 2021/6/11 15:12
 */
@Configuration
@ConditionalOnProperty(
		prefix = PropertiesConstants.MINIO_CONFIG_PROPERTIES_PREFIX,
		name = PropertiesConstants.ENABLE,
		havingValue = Constants.TRUE
)
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioConfig {

	@Bean
	@ConditionalOnMissingBean
	public MinioClient minioClient(MinioConfigProperties minioConfigProperties) {
		return MinioClient.builder()
				.endpoint(minioConfigProperties.getEndpoint())
				.credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public MinioClientX minioClientX(MinioClient minioClient, MinioConfigProperties minioConfigProperties) {
		return new MinioClientX(minioClient, minioConfigProperties);
	}
}
