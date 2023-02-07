package com.liushijie.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class User {
    private Long id; // 用户编号
    private Integer userType; // 用户类型  1：管理员 2：普通用户 默认普通用户
    private String username; // 用户名
    private String password; // 密码
    private String userEmail; // 邮箱
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 注册时间
    private String userSignature; // 个性签名
    private String userIcon; // 个人头像
}
