package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 重置密码验证码QO类
 */
@Data
public class UserResetQo {
    @NotBlank(message = "密码不能为空")
    private String password;//密码
    @NotBlank(message = "邮箱不能为空")
    private String userEmail;//邮箱
    @NotBlank(message = "验证码不能为空")
    private String token;//重置密码的临时token
}
