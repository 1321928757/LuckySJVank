package com.liushijie.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Long messageId; // 留言编号
    private String messageQQ; // 留言QQ
    private String messageName; // 留言昵称
    private String content; // 留言内容
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date messageDate; // 留言日期
}
