package com.liushijie.vo;

import com.liushijie.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class LoginSuccessVo {
    private User user;
    private String token;
}
