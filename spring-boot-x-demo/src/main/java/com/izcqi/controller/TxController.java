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

import com.izcqi.entity.UserEntity;
import com.izcqi.repo.UserRepo;
import org.openingo.spring.boot.extension.datasource.tx.TransactionTemplateX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TxController
 *
 * @author Qicz
 * @since 2021/6/28 16:46
 */
@RestController
@RequestMapping("tx")
public class TxController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TransactionTemplateX transactionTemplateX;

	@PostMapping("/save")
	public String save(@RequestBody  UserEntity user) {
		userRepo.save(user);
		return "ok";
	}

	private UserEntity saveUser(UserEntity user) {
		return userRepo.save(user);
	}

	private void saveWithEx(UserEntity user) {
		saveUser(user);
		throw new RuntimeException("error");
	}

	@PostMapping("/savex")
	public String saveEx(@RequestBody  UserEntity user) {
		transactionTemplateX.txRun(() -> this.saveWithEx(user));
		return "ok";
	}

	@PostMapping("/savexe")
	public String saveExe(@RequestBody  UserEntity user) {
		transactionTemplateX.txCall(() ->  {
			this.saveUser(user);
			throw new RuntimeException("error");
		});
		return "ok";
	}
}
