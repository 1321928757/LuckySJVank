package com.liushijie.qo;


import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 后台文章管理页获取文章QO
 */
@Data
public class BackSearchArticleQo {
    @NotNull(message = "网站分类不能为空")
    private Integer page; //当前页码
    @NotNull(message = "网站分类不能为空")
    private Integer pageSize; //页码大小
    @NotNull(message = "网站分类不能为空")
    private Integer state; //文章状态
    @NotNull(message = "搜索关键字不能为NULL（可为空字符串）")
    private String key; //搜索关键字
}
