package com.liushijie;

import com.liushijie.config.CosConfig;
import com.liushijie.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LuckysjApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LuckysjApplication.class, args);
        SpringContextUtil contextUtil = new SpringContextUtil();
        contextUtil.setApplicationContext(run);
    }

}
