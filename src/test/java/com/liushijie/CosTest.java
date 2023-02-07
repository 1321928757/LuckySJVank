package com.liushijie;

import com.liushijie.config.CosConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CosTest {
    @Autowired
    private CosConfig cosConfig;
    @Value("${cos.accessKey}")
    private String accessKey;
    @Value("${cos.secretKey}")
    private String secretKey;
    @Value("${cos.regionName}")
    private String regionName;
    @Value("${cos.bucketName}")
    private String bucketName;
    @Value("${cos.baseUrl}")
    private String baseUrl;
    @Value("${cos.folderPrefix}")
    private String folderPrefix;
    @Test
    public void test(){
        System.out.println(accessKey);
    }
}
