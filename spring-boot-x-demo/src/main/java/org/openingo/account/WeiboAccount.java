package org.openingo.account;

import lombok.Data;
import org.openingo.spring.validator.DynamicValidator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * WeiboAccount
 *
 * @author Qicz
 * @since 2021/4/21 11:17
 */
@Data
public class WeiboAccount implements Serializable, DynamicValidator {

    @NotBlank(message = "账号id不能为空")
    private String accountId;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "性别不能为空")
    @Pattern(regexp = "^man$|^woman$", message = "取值不合法")
    private String sex;

    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "只能18岁以上用户可以使用")
    private Integer age;

    @NotBlank(message = "头像不能为空")
    private String avatarUri;

    @NotBlank(message = "签名不能为空")
    private String signature;

    @Override
    public void dynamicValidate() {
        // 在这个场景中，WeiboAccount其实与IdCard相当，都是最后一级校验，
        // 所以可以不必实现DynamicValidator
    }
}
