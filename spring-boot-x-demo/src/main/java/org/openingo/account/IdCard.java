package org.openingo.account;

import lombok.Data;
import org.openingo.spring.validator.DynamicValidator;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * IdCard
 *
 * @author Qicz
 * @since 2021/4/21 11:27
 */
@Data
public class IdCard implements Serializable, DynamicValidator {

    @NotBlank(message = "id不能为null")
    private String id;

    @NotBlank(message = "address不能为null")
    private String address;

    @Override
    public void dynamicValidate() {
        // 啥也不用写
    }
}
