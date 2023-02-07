package com.liushijie.utils;

import java.util.Random;

/**
 * 验证码生成工具类
 */
public class RandUtils {
    /**
     * 生成count长度的验证码
     * @param count
     * @return
     */
    public static String getRandomCode(Integer count){
        Random r = new Random();
        String includes = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String code = "";
        for (int i = 0; i < count; i++) {
            Integer index = r.nextInt(36);
            code += includes.charAt(index);
        }

        return code;
    }
}
