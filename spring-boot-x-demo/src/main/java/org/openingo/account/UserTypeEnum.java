package org.openingo.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * UserTypeEnum
 *
 * @author Qicz
 * @since 2021/4/21 11:18
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    WECHAT(1),
    WEIBO(2),
    ;

    private Integer code;

    @JsonCreator
    public static UserTypeEnum newByCode(Integer code) {
        return Stream.of(values()).filter(e -> code.equals(e.getCode())).findFirst().orElse(null);
    }
}
