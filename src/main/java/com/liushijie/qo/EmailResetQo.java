package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//重置邮箱的Qo类
@Data
public class EmailResetQo {
    @NotNull(message = "用户id不能为空密码不能为空")
    private Long id;//用户id
    @NotBlank(message = "新邮箱不能为空")
    private String userEmail;//新邮箱
    @NotBlank(message = "token不能为空")
    private String token;//旧邮箱校验通过的token
    @NotBlank(message = "验证码不能为空")
    private String vericode;//新邮箱验证码
}
