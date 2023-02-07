package com.liushijie.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//评论实体类
@Data
public class Comment {
    // 评论基本信息
    private Long id;           // 评论id
    private String content;              // 评论内容
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commentDate;            // 评论日期
    private Long articleId; // 文章编号
    private Long userId; // 评论用户编号
    private Long parentId; // 父评论的id没有则为0
    private Integer setTop; // 评论是否置顶
}
