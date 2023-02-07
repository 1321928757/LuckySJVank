package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//后台修改密码Qo类
@Data
public class PwdResetQo {
    @NotNull(message = "用户id不能为空")
    private Long id;//用户id
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;//旧密码
    @NotBlank(message = "新密码不能为空")
    private String newPassword;//新密码
}
