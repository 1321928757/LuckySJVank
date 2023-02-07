package com.liushijie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liushijie.entity.User;

public interface UserService extends IService<User> {
    User seekUser(String username);
}
