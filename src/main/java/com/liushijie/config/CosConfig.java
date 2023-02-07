package com.liushijie.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云Cos对象储存配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "cos")
public class CosConfig {
    private String baseUrl;
    private String accessKey;
    private String secretKey;
    private String regionName;
    private String bucketName;
    private String folderPrefix;
}
