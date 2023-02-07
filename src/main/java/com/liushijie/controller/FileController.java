package com.liushijie.controller;

import com.liushijie.common.CustomException;
import com.liushijie.common.R;
import com.liushijie.service.impl.CosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//文件上传下载控制类
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {

    /*本地文件存放的根目录（本地储存，这里目前用的是COS对象储存）*/
    @Value("${file.baseDir}")
    private String basePath;

    private String[] imgTypes = {".png", ".jpg", ".svg", ".gif", ".jpeg"};
    private String[] videoTypes = { ".mp4", ".avi", ".wmv",
        ".mpeg", ".m4v", ".mov", ".asf", ".flv", ".f4v", ".rmvb", ".rm", ".3gp", ".vob"
    };

    @Autowired
    private CosService cosService;
    /**
     * 小型图片文件上传,只能上传2MB以下的图片文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        if(file == null) {
            return R.error("文件为空");
        }
        //大小不能超过五百kb
        if(file.getSize() / 1024 > 2048){
            log.warn("文件上传失败，原因：大小过大");
            return R.error("上传失败，图片不能大于2MB");
        }

        Integer flag = 0;
        /*获取对应文件名*/
        String originName = file.getOriginalFilename();
        log.info(originName + "文件上传中~~~~~");
        /*截取文件后缀名*/
        String suffix = originName.substring(originName.lastIndexOf("."));

        /*判断文件类型*/
        for (String type : imgTypes) {
            if(type.equals(suffix)) flag = 1;
        }
        if(flag == 0) {
            log.warn("文件上传失败，原因：文件类型不支持");
            return R.error("上传失败，文件类型不支持");
        };
        String path = null;

        try {
            /*上传到腾讯云*/
            path = cosService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("文件上传失败");
        }

        /*返回生成的对应文件名*/
        return R.success(path);
    }

    /**
     * 正常图片文件上传,能上传5MB以下的图片
     * @param file
     * @return
     */
    @PostMapping("/upload/img")
    public R<String> upload2(MultipartFile file){
        if(file == null) {
            return R.error("文件为空");
        }

        //大小不能超过5MB
        if(file.getSize() / 1024 > 5120){
            log.warn("文件上传失败，原因：大小过大");
            return R.error("上传失败，图片不能大于5MB");
        }

        Integer flag = 0;
        /*获取对应文件名*/
        String originName = file.getOriginalFilename();
        log.info(originName + "文件上传中~~~~~");
        /*截取文件后缀名*/
        String suffix = originName.substring(originName.lastIndexOf("."));

        /*判断文件类型*/
        for (String type : imgTypes) {
            if(type.equals(suffix)) flag = 1;
        }
        if(flag == 0) {
            log.warn("文件上传失败，原因：文件类型不支持");
            return R.error("上传失败，文件类型不支持");
        };
        String path = null;

        try {
            /*上传到腾讯云*/
            path = cosService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("文件上传失败");
        }
        /*返回生成的对应文件名*/
        return R.success(path);
    }

    /**
     * 正常视频文件上传,能上传100MB以下的视频
     * @param file
     * @return
     */
    @PostMapping("/upload/video")
    public R<String> upload3(MultipartFile file){
        if(file == null) {
            return R.error("文件为空");
        }

        //大小不能超过5MB
        if(file.getSize() / 1024 > 102400){
            log.warn("文件上传失败，原因：大小过大");
            return R.error("上传失败，视频不能大于100MB");
        }

        Integer flag = 0;
        /*获取对应文件名*/
        String originName = file.getOriginalFilename();
        log.info(originName + "文件上传中~~~~~");
        /*截取文件后缀名*/
        String suffix = originName.substring(originName.lastIndexOf("."));

        /*判断文件类型*/
        for (String type : videoTypes) {
            if(type.equals(suffix)) flag = 1;
            System.out.println(type + "||||" + suffix);
        }
        if(flag == 0) {
            log.warn("文件上传失败，原因：文件类型不支持");
            return R.error("上传失败，文件类型不支持");
        };
        String path = null;

        try {
            /*上传到腾讯云*/
            path = cosService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("文件上传失败");
        }
        /*返回生成的对应文件名*/
        return R.success(path);
    }
    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try (
                /*创建输入流，读取文件*/
                FileInputStream inputStream = new FileInputStream(new File(basePath + name));
                /*创建输出流*/
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            /*设置响应文件的格式为照片*/
            response.setContentType("image/jpeg");

            /*读取文件并将流输出给前端*/
            int len = 0;
            byte[] butters = new byte[1024];
            while((len = inputStream.read(butters)) != -1){
                outputStream.write(butters, 0, len);
                /*刷新流对象*/
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
