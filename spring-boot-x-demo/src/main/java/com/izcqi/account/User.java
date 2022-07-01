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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User
 *
 * @author Qicz
 * @since 2021/4/21 11:17
 */
@Data
public class User implements Serializable, DynamicValidator {

    @NotNull(message = "用户类型不可为null")
    private UserTypeEnum userType;

    private WeChatAccount weChatAccount;

    private WeiboAccount weiboAccount;

    @Override
    public void dynamicValidate() {
        // 根据用户的类型确定要校验的数据
        if (UserTypeEnum.WECHAT.equals(this.userType)) {
            // 直接调用dynamicValidate校验对应的字段，
            // 会进行非null及字段的dynamicValidate的进一步校验
            this.dynamicValidate(this.weChatAccount, "微信数据不合法");
        } else {
            this.dynamicValidate(this.weiboAccount, "微博数据不合法");
        }
    }
}
