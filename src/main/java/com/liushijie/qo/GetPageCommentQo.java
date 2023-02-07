package com.liushijie.qo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 分页查询评论Qo类
 */
@Data
public class GetPageCommentQo {
    @NotNull(message = "文章id不能为空")
    private Long articleId; //文章id
    @NotNull(message = "搜索当前页不能为空")
    private Integer page; //当前页
    @NotNull(message = "搜索页码大小不能为空")
    private Integer pageSize; //页码大小
}
