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

package org.openingo.spring.boot.extension.minio;

import io.minio.MinioClient;
import io.minio.PostPolicy;
import lombok.SneakyThrows;
import org.openingo.spring.boot.extension.minio.config.MinioConfigProperties;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * MinioClientX
 *
 * @author Qicz
 * @since 2021/6/11 15:56
 */
public class MinioClientX extends MinioClient {

	private final MinioConfigProperties minioConfigProperties;

	public MinioClientX(MinioClient client, MinioConfigProperties minioConfigProperties) {
		super(client);
		this.minioConfigProperties = minioConfigProperties;
	}

	/**
	 * get presigned post form data
	 *
	 * @param objectName the object name with file extension
	 * @return {@link Map<String, String>}
	 */
	@SneakyThrows
	public Map<String, String> getPresignedPostFormData(String objectName) {
		String allowFileTypes = this.minioConfigProperties.getAllowFileTypes().toLowerCase();
		int idx = objectName.lastIndexOf(".");
		Assert.isTrue(-1 != idx, "file extension is unknown");
		String fileType = objectName.substring(idx + 1).toLowerCase();
		Assert.isTrue(allowFileTypes.contains(fileType), String.format("the \"%s\" file is not allow", fileType));
		String bucket = this.minioConfigProperties.getBucket();
		Integer uploadExpireSeconds = this.minioConfigProperties.getUploadExpireSeconds();
		Integer allowMinSize = this.minioConfigProperties.getAllowMinSize();
		Integer allowMaxSize = this.minioConfigProperties.getAllowMaxSize();
		PostPolicy policy = new PostPolicy(bucket, ZonedDateTime.now().plusSeconds(uploadExpireSeconds));
		// Add condition that 'key' (object name) equals to objectName.
		policy.addEqualsCondition("key", objectName);
		// Add condition that 'content-length-range' is between minSize kib to maxSize kib.
		policy.addContentLengthRangeCondition(allowMinSize, allowMaxSize);
		return this.getPresignedPostFormData(policy);
	}
}
