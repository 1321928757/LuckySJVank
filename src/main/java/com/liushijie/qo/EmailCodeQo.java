package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 邮箱验证码校验Qo
 */
@Data
public class EmailCodeQo {
    @NotBlank(message = "验证码缓存key不能为空")
    private String userEmail;//验证码存在缓存中对应的key，例如图形验证码的uuid，邮箱验证码的邮箱号码
    @NotBlank(message = "验证码不能为空")
    private String vericode;//验证码
}
