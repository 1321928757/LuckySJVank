package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//申请链接Qo类
@Data
public class ApplySiteQo {
    @NotBlank(message = "网站名称不能为空")
    private String urlName; // 网站名称
    @NotBlank(message = "网站地址不能为空")
    private String urlAddres; // 网站地址
    @NotBlank(message = "网站简介不能为空")
    private String urlReferral; // 网站介绍
    @NotBlank(message = "网站封面不能为空")
    private String urlLitimg;// 网站缩略图
    @NotBlank(message = "站长邮箱不能为空")
    private String webmasterEmail; // 站长邮箱
    @NotNull(message = "网站分类不能为空")
    private Integer urlType; // 链接类型  1：学习网站 2：友情链接
}
