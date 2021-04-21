package org.openingo.account;

import lombok.Data;
import org.openingo.spring.validator.DynamicValidator;

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
