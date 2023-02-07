package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文章搜索vo
 */
@Data
public class SearchAirticleQo {
    @NotNull(message = "搜索关键字不能为空")
    private String key; //关键字
    @NotNull(message = "搜索当前页不能为空")
    private Integer page; //当前页
    @NotNull(message = "搜索页码大小不能为空")
    private Integer pageSize; //页码大小
    @NotNull(message = "搜索排序类型不能为空")
    private Integer type; //排序类型 1:时间排序，2:浏览量排序，3:评论数排序
    @NotNull(message = "搜索排序方式不能为空")
    private Integer type2; //排序类型 1:按关键字，2:按分类
}
