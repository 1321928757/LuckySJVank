package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录消息类
 */
@Data
public class UserLoginQo {
    @NotBlank(message = "用户名不能为空")
    private String username;//用户名
    @NotBlank(message = "密码不能为空")
    private String password;//密码
    @NotBlank(message = "验证码不能为空")
    private String vericode;//验证码
    @NotBlank(message = "验证uuid不能为空")
    private String codeUuid;//验证码对应的uuid
}
