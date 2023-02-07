package com.liushijie.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Airticle {
    private Long id; // 文章编号
    private Long userId; // 编写用户编号
    private String userName; // 编写用户名称
    private String articleTitle; // 文章标题
    private Long articleClassifyId; // 文章分类id
    private String articleClassifyName; // 文章分类名称
    private String articleDase; // 文章描述
    private String articleImg; // 文章缩略图
    private String articleContent; // 文章内容
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date publishTime; // 发表时间
    private Integer articlePass; // 审核状态
    private Integer articleState; // 文章状态 所有人 仅自己
    private Integer commentState;// 评论状态 开启 关闭
    private Integer click; // 阅读次数
    private Integer review; // 评论次数
}
