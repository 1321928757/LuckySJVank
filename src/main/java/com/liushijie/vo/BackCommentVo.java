package com.liushijie.vo;

import com.liushijie.entity.Comment;
import lombok.Data;

/**
 * 后台评论查询VO类
 */
@Data
public class BackCommentVo extends Comment {
    private String username;
    private String parentName;
    private String parentContent;
    private String airticleName;
}
