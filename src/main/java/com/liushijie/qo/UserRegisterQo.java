package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册Qo类
 */
@Data
public class UserRegisterQo {
    @NotBlank(message = "用户名不能为空")
    private String username;//用户名
    @NotBlank(message = "密码不能为空")
    private String password;//密码
    @NotBlank(message = "邮箱不能为空")
    private String userEmail;//邮箱
    @NotBlank(message = "验证码不能为空")
    private String vericode;//验证码
}
