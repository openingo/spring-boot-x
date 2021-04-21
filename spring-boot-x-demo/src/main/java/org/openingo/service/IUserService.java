package org.openingo.service;

import org.openingo.account.User;

/**
 * syncUser
 *
 * @author Qicz
 * @since 2021/4/21 14:16
 */
public interface IUserService {

    boolean syncUser(User user);
}
