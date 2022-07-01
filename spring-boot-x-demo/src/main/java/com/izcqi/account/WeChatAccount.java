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

package com.izcqi.account;

import lombok.Data;
import org.openingo.spring.boot.kit.validator.DynamicValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * WeChatAccount
 *
 * @author Qicz
 * @since 2021/4/21 11:17
 */
@Data
public class WeChatAccount implements Serializable, DynamicValidator {

    @NotBlank(message = "账号id不能为空")
    private String accountId;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "性别不能为空")
    @Pattern(regexp = "^man$|^woman$", message = "取值不合法")
    private String sex;

    @NotNull(message = "年龄不能为空")
    private Integer age;

    @NotBlank(message = "头像不能为空")
    private String avatarUri;

    private IdCard idCard;

    @Override
    public void dynamicValidate() {
        // 如果年龄大于18岁，需要校验身份信息是否合法
        if (this.age >= 18) {
            // 同样的dynamicValidate将对idCard进行非null及进一步的校验
            this.dynamicValidate(this.idCard, "身份信息不合法");
            // IdCard如未实现`DynamicValidator`可以按照如下方式进行校验
            // this.validateField(null == this.idCard, "身份信息不合法");
            // this.validateSelf(this.idCard);
        }
    }
}
