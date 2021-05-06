package org.openingo.controller;

import org.openingo.account.User;
import org.openingo.controller.api.UserApi;
import org.openingo.jdkits.http.RespData;
import org.openingo.service.IUserService;
import org.openingo.spring.http.response.annotation.AutoMappingRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author Qicz
 * @since 2021/4/21 13:54
 */
@RestController
@Validated
@AutoMappingRespResult
public class UserController implements UserApi {

    @Autowired
    IUserService userService;

    @PostMapping("/sync")
    public RespData syncUser(@RequestBody @Validated User user) {
        user.dynamicValidate();
        this.userService.syncUser(user);
        return RespData.success();
    }

    @Override
    public boolean add(User user) {
        return false;
    }

    @Override
    public boolean edit(User user) {
        return false;
    }
}
