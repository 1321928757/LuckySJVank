package com.liushijie.vo;

import lombok.Data;

import java.util.UUID;

/**
 * 图形验证码返回vo类
 */
@Data
public class ImgCodeVo {
    private UUID uuid;
    private String codeImg;
}
