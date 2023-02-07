package com.liushijie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * 邮箱发送邮件工具类
 */
@Component
public class EmailUtils {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Value("${spring.mail.username}")
    private String username;

    /**
     * 向邮箱发送指定内容邮件
     *
     * @param email   邮箱地址
     * @param content 邮件内容
     * @return
     */
    public void sendCode(String email, String content, String title) {
        //简单邮件
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("LukcySJ<" + username + ">");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        mailSender.send(simpleMailMessage);
    }
}
