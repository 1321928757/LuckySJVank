package com.liushijie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
public class Mailtest {
    @Autowired
    private JavaMailSenderImpl mailSender;

    @Test
    public void sendTest(){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("1321928757@qq.com");
        mailMessage.setTo("1321928757@qq.com");
        mailMessage.setSubject("测试");
        mailMessage.setText("11111");
        mailSender.send(mailMessage);
    }
}
