package org.openingo.controller;

import org.openingo.account.User;
import org.openingo.jdkits.http.RespData;
import org.openingo.service.IUserService;
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
public class UserController {

    @Autowired
    IUserService userService;

    @PostMapping("/user/sync")
    public RespData syncUser(@RequestBody @Validated User user) {
        user.dynamicValidate();
        this.userService.syncUser(user);
        return RespData.success();
    }
}
