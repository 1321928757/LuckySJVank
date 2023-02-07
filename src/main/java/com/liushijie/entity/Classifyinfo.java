package com.liushijie.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import lombok.Data;

@Data
public class Classifyinfo {
    private Long id; // 分类编号
    private String classifyName; // 分类名称
    private Integer articleNumber; // 文章数量
    private String color1;// 渐变色1,
    private String color2;// 渐变色2
    private String classifyIntroduce; // 分类介绍
}
