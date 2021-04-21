package org.openingo.service.impl;

import org.openingo.account.User;
import org.openingo.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author Qicz
 * @since 2021/4/21 14:17
 */
@Service
public class UserServiceImpl implements IUserService {

    @Override
    public boolean syncUser(User user) {
        // 使用 dubbo 时，最好在provider再次校验
        // user.dynamicValidate();
        return false;
    }
}
