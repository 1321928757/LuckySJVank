package com.liushijie.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 保存留言qo类
 */
@Data
public class SaveMessageQo {
    @NotBlank(message = "留言QQ不能为空")
    private String messageQQ; // 留言QQ
    @NotBlank(message = "留言昵称不能为空")
    private String messageName; // 留言昵称
    @NotBlank(message = "留言内容不能为空")
    private String content; // 留言内容
    @NotBlank(message = "图形验证码不能为空")
    private String code; //图形验证码
    @NotBlank(message = "验证码uuid不能为空")
    private String uuid; //图形验证码的uuid
}
