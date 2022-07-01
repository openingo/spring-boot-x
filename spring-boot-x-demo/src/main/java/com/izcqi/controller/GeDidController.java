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

package com.izcqi.controller;

import org.openingo.jdkits.sys.SystemClockKit;
import org.openingo.spring.boot.extension.data.redis.RedisStringKeyTemplateX;
import org.openingo.spring.boot.extension.data.redis.RedisTemplateX;
import org.openingo.spring.boot.extension.gedid.loader.DidLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * GeDidController
 *
 * @author Qicz
 * @since 2021/6/23 13:40
 */
@RestController
public class GeDidController {

	@Autowired
	RedisTemplateX redisTemplateX;

	@Autowired
	RedisStringKeyTemplateX redisStringKeyTemplateX;

	@Autowired
	DidLoader didLoader;

	@GetMapping("/cc/{name}")
	public String cc(@PathVariable("name") String name) {
		String ret = "";
		if ("uuid".equals(name)) {
			ret = didLoader.next("uuid");
		} else {
			Long next = didLoader.next(name);
			ret = next.toString();
		}
		System.out.println(ret);
		redisStringKeyTemplateX.set("zcq", "zcqqicz" + SystemClockKit.now());
		return String.format("next = %s", ret + redisStringKeyTemplateX.get("zcq"));
	}


	@GetMapping("/add/{name}")
	public String addBusiness(@PathVariable("name") String businessName) {
		this.didLoader.follow("redis", businessName);
		return "ok";
	}
}
