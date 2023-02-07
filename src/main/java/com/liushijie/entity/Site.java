package com.liushijie.entity;

import lombok.Data;

/**
 * 网站链接类
 */
@Data
public class Site {
    private Long id; // 链接编号
    private String urlName; // 网站名称
    private String urlAddres; // 网站地址
    private String urlReferral; // 网站介绍
    private String urlLitimg;// 网站缩略图
    private String webmasterEmail; // 站长邮箱
    private Integer urlPass; // 后台审核是否通过 1待审核 2审核通过 3审核失败
    private Integer urlType; // 链接类型  1：学习网站 2：友情链接
}
