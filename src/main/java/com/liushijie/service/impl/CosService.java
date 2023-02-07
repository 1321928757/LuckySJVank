package com.liushijie.service.impl;

import com.liushijie.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 腾讯云COS文件上传服务层
 */
@Slf4j
@Service
public class CosService {
    /**
     * 配置
     */
    private final CosConfig cosConfig;
    /**
     * 初始化用户身份信息
     */
    private final COSCredentials cred;

    /**
     * 设置bucket的区域
     */
    private final ClientConfig clientConfig;

    /**
     * 生成COS客户端
     */
    private final COSClient cosClient;

    @Autowired
    public CosService(CosConfig cosConfig) {
        this.cosConfig = cosConfig;
        this.cred = new BasicCOSCredentials(cosConfig.getAccessKey(), cosConfig.getSecretKey());
        this.clientConfig = new ClientConfig(new Region(cosConfig.getRegionName()));
        this.cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String upload(MultipartFile file) throws Exception {
        /*通过uuid生成随机文件名*/
        String uuid = UUID.randomUUID().toString();
        /*获取文件名*/
        String originName = file.getOriginalFilename();
        log.info("upload COS is Going: {}", originName);
        /*截取文件后缀名*/
        String suffix = originName.substring(originName.lastIndexOf("."));
        /*拼接uuid与后缀组成文件名*/
        String key = uuid + suffix;

        File localFile = null;
        try {
            localFile = transferToFile(file);
            String filePath = uploadFileToCOS(localFile, key);
            log.info("upload COS successful: {}", filePath);
            return filePath;
        } catch (Exception e) {
            throw e;
        } finally {
            if(localFile != null){
                localFile.delete();
            }
        }
    }

    /**
     * 上传文件到COS
     *
     * @param localFile
     * @param key
     * @return
     */
    private String uploadFileToCOS(File localFile, String key) throws InterruptedException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), key, localFile);
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        // 传入一个threadPool, 若不传入线程池, 默认TransferManager中会生成一个单线程的线程池
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        // 返回一个异步结果Upload, 可同步的调用waitForUploadResult等待upload结束, 成功返回UploadResult, 失败抛出异常
        Upload upload = transferManager.upload(putObjectRequest);
        UploadResult uploadResult = upload.waitForUploadResult();
        transferManager.shutdownNow();
        cosClient.shutdown();
        String filePath = cosConfig.getBaseUrl() + "/" + uploadResult.getKey();
        return filePath;
    }

    /**
     * 用缓冲区来实现这个转换, 即创建临时文件
     * 使用 MultipartFile.transferTo()
     *
     * @param multipartFile
     * @return
     */
    private File transferToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String prefix = originalFilename.split("\\.")[0];
//        前缀长度不能小于3(不然报错)
        if(prefix.length() < 3){
            prefix += "111";
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);
        return file;
    }

}
