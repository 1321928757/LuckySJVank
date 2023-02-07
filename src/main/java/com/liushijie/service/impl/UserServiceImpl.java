package com.liushijie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.User;
import com.liushijie.mapper.UserMapper;
import com.liushijie.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/**
 * 用户类服务层
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User seekUser(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
        queryWrapper.eq(Strings.isNotEmpty(username), User::getUsername, username);
        User user = this.getOne(queryWrapper);
        return user;
    }
}
