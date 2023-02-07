/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : luckysj

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 11/01/2023 16:47:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for airticle
-- ----------------------------
DROP TABLE IF EXISTS `airticle`;
CREATE TABLE `airticle`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章编号',
  `userId` bigint NOT NULL COMMENT '编写用户编号',
  `userName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编写用户名称',
  `articleTitle` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文章标题',
  `articleClassifyId` bigint NOT NULL COMMENT '文章分类id',
  `articleClassifyName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章分类名称',
  `articleDase` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文章描述',
  `articleImg` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文章缩略图',
  `articleContent` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文章内容',
  `publishTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发表时间',
  `articleState` int NOT NULL DEFAULT 1 COMMENT '文章状态',
  `articlePass` int NULL DEFAULT 1 COMMENT '后台审核是否通过：0待审核 1已通过 2已拒绝',
  `commentState` int NOT NULL DEFAULT 1 COMMENT '评论状态',
  `click` int NOT NULL DEFAULT 0 COMMENT '阅读次数',
  `review` int NOT NULL DEFAULT 0 COMMENT '评论次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_articleInfo_userId`(`userId` ASC) USING BTREE,
  INDEX `fk_articleInfo_articleClassifyId`(`articleClassifyId` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1612741268599738371 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文章管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airticle
-- ----------------------------

-- ----------------------------
-- Table structure for classifyinfo
-- ----------------------------
DROP TABLE IF EXISTS `classifyinfo`;
CREATE TABLE `classifyinfo`  (
  `classifyIntroduce` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类介绍',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `classifyName` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `articleNumber` int NOT NULL DEFAULT 0 COMMENT '文章数量',
  `color1` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '渐变色1',
  `color2` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '渐变色2',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1610980570425233411 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分类管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classifyinfo
-- ----------------------------
INSERT INTO `classifyinfo` VALUES ('日常挖坑集', 1, '技术', 0, '#6D80FE', '#23D2FD');
INSERT INTO `classifyinfo` VALUES ('创意无限大', 2, '资源', 0, '#FBF40F', '#FFA9AB');
INSERT INTO `classifyinfo` VALUES ('七七八八十分精彩', 3, '杂文', 0, '#09B0E8', '#29F49A');
INSERT INTO `classifyinfo` VALUES ('记录美好生活', 4, '生活', 0, '#717CFE', '#FC83EC');
INSERT INTO `classifyinfo` VALUES ('emo患者区', 5, '情感', 0, '#535b9a', '#30bcd7');
INSERT INTO `classifyinfo` VALUES ('踩坑喜加一', 6, '其他', 0, '#FF988B', '#FF6B88');
INSERT INTO `classifyinfo` VALUES ('测试', 1610966150789754882, 'Cesar', 0, '#D96B6B', '#9F8181');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '评论编号',
  `articleId` bigint NOT NULL COMMENT '文章代号',
  `userId` bigint NOT NULL COMMENT '评论用户编号',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
  `parentId` bigint NULL DEFAULT 0 COMMENT '父评论',
  `commentDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论日期',
  `setTop` int NOT NULL DEFAULT 0 COMMENT '是否置顶：0不置顶 1置顶',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for logininfo
-- ----------------------------
DROP TABLE IF EXISTS `logininfo`;
CREATE TABLE `logininfo`  (
  `loginId` bigint NOT NULL COMMENT '登录的用户编号',
  `loginTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '登录管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logininfo
-- ----------------------------
INSERT INTO `logininfo` VALUES (1, '2022-11-02 17:19:36');
INSERT INTO `logininfo` VALUES (1, '2022-11-03 09:40:57');
INSERT INTO `logininfo` VALUES (1, '2022-11-03 13:02:24');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `messageId` bigint NOT NULL AUTO_INCREMENT COMMENT '留言编号',
  `messageQQ` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言QQ',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言内容',
  `messageDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言日期',
  `messageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言者名称',
  PRIMARY KEY (`messageId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 123123123123131 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for site
-- ----------------------------
DROP TABLE IF EXISTS `site`;
CREATE TABLE `site`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '链接编号',
  `urlName` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网站名称',
  `urlAddres` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网站地址',
  `urlReferral` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网站介绍',
  `urlLitimg` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网站缩略图',
  `webmasterEmail` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '站长邮箱',
  `urlPass` int NULL DEFAULT 0 COMMENT '后台审核是否通过',
  `urlType` int NULL DEFAULT NULL COMMENT '链接类型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `urlAddres`(`urlAddres` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1611983011652419586 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '链接管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of site
-- ----------------------------

-- ----------------------------
-- Table structure for systemsetup
-- ----------------------------
DROP TABLE IF EXISTS `systemsetup`;
CREATE TABLE `systemsetup`  (
  `effects02` tinyint(1) NULL DEFAULT 0 COMMENT '特效2',
  `effects01` tinyint(1) NULL DEFAULT 0 COMMENT '特效1',
  `stickArticle` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '置顶',
  `allArticle` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章展示',
  `featuredArticle` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧精选文章',
  `technologyArticle` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧技术专区文章',
  `resourceArticle` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '右侧资源专区文章',
  `advertising1` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧广告图1',
  `advertisingLink1` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧广告链接1',
  `advertising2` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '右侧广告图1',
  `advertisingLink2` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧广告链接1',
  `advertising3` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '右侧广告图2',
  `advertisingLink3` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '左侧广告链接1',
  `mouseEffect` int NULL DEFAULT 0 COMMENT '鼠标跟附特效开关',
  `mouseEffectImg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '鼠标跟附特效图片',
  `effect1` int NULL DEFAULT 0 COMMENT '特效1开关',
  `effect2` int NULL DEFAULT 0 COMMENT '特效2开关',
  `id` int NOT NULL,
  `carouseImg1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图1图片',
  `carouseText1` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图1标语',
  `carouseImg2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图2图片',
  `carouseText2` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图2标语',
  `carouseImg3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图3图片',
  `carouseText3` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图3标语',
  `carouseImg4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图4图片',
  `carouseText4` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图4标语',
  `carouseUrl1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图1链接',
  `carouseUrl2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图2链接',
  `carouseUrl3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图3链接',
  `carouseUrl4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图4链接',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of systemsetup
-- ----------------------------
INSERT INTO `systemsetup` VALUES (0, 0, '1610191745281196033', '[\"1610172573071446017\",\"1610140401857986561\",\"1610140650454384641\",\"1610140628283293698\"]', '[\"1612693416766922753\"]', '[\"1612693462728105986\"]', '[\"1610140628283293698\"]', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/3.png', '', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/e689439c8d3444dab66382adea085fa8.jpg', '', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/1686e9cab38e46abb370f15a7baacd10.jpg', '', 1, 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/c814149a-c8f4-4ead-9f61-8da6684e7cb4.gif', 0, 1, 1, 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/2fa97baf-730d-4562-adeb-3fa48da09962.jpg', '花看半开，酒饮微醺', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/7ac126b9-793c-444b-a4cb-1e3373fc5fc1.jpg', '梦里不知身是客，一响贪欢', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/bc51ee0b-5053-481c-8273-7bd7b13c3959.jpg', '柴门闻犬吠，风雪夜归人', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/32b4899d-37a1-4740-b7f1-4262dd60adea.jpg', NULL, '', '', NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `userType` int NOT NULL DEFAULT 1 COMMENT '用户类型：0管理员，1正常用户，6站长（超级权限）',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `userEmail` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `userSignature` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '该用户很懒，未设置签名' COMMENT '个性签名',
  `userIcon` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个人头像',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `userName`(`username` ASC) USING BTREE,
  UNIQUE INDEX `userEmail`(`userEmail` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1611292630879047683 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '注册用户管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (58, 0, 'kitie', 'lsj20030222', '1321928757@qq.com', '2022-11-17 11:37:33', '该用户很懒，未设置签名', 'https://luckysj-1314434715.cos.ap-shanghai.myqcloud.com/ad86a169-049f-4f4a-89f8-56e902bf812c.jpg');

SET FOREIGN_KEY_CHECKS = 1;
