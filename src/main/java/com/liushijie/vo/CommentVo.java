package com.liushijie.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liushijie.entity.Comment;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 评论Vo类
 */
@Data
public class CommentVo{
    // 评论基本信息
    private Long id;           // 评论id
    private String content;              // 评论内容
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commentDate;            // 评论日期
    private Long articleId; // 文章编号   这里考虑到实际情况下：因为blog本身可能很大，在前后端传输数据时会影响效率，所以comment没必要再包含文章id
    private Long userId; // 评论用户编号
    private Long parentId; // 父评论的id没有则为0
    private Integer setTop; //评论是否置顶
    // 多表查询到的字段
    private Integer userType; // 用户类型  1：管理员 2：普通用户 默认普通用户
    private String username; // 用户名
    private String userEmail; // 邮箱
    private Date userRegdate; // 注册时间
    private String userSignature; // 个性签名
    private String userIcon; // 个人头像

    private List<CommentVo> child = new ArrayList<>(); // 本评论下的子评论
}
