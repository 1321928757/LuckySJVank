package com.liushijie.entity;

import lombok.Data;

/**
 * 系统配置类
 */
@Data
public class Systemsetup {
    private Long id; //id
    private String stickArticle;// '置顶',
    private String allArticle;// '文章展示',
    private String featuredArticle;// '左侧精选文章',
    private String technologyArticle;// '左侧技术专区文章',
    private String resourceArticle;// '右侧资源专区文章',
    private String advertising1;// '左侧广告图1',
    private String advertisingLink1;// '左侧广告链接1',
    private String advertising2;// '右侧广告图1',
    private String advertisingLink2;// '左侧广告链接1',
    private String advertising3;// '右侧广告图2',
    private String advertisingLink3;// '左侧广告链接1'
    private Integer mouseEffect; //鼠标跟随特效开关
    private String mouseEffectImg; //鼠标跟随特效的图片
    private Integer effect1; //特效1开关
    private Integer effect2; //特效2开关
    //轮播图
    private String carouseImg1;//图片
    private String carouseText1;//标语
    private String carouseUrl1;//链接
    private String carouseImg2;
    private String carouseText2;
    private String carouseUrl2;
    private String carouseImg3;
    private String carouseText3;
    private String carouseUrl3;
    private String carouseImg4;
    private String carouseText4;
    private String carouseUrl4;
}
