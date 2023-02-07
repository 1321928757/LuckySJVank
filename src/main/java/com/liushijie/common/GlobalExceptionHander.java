package com.liushijie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.WebFilter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, WebFilter.class})
public class GlobalExceptionHander {

    /*拦截字段校验失败的异常*/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleBindGetException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());
        return R.error("字段校验未通过，请将信息填写完整");
    }

    /*异常处理器，拦截自定义的企业级异常*/
    @ExceptionHandler(CustomException.class)
    public R<String> ExceptionHandler(CustomException exception){
        log.error("拦截到企业级异常:" + exception.getMessage());

        /*数据库主键重复异常*/
        if(exception.getMessage().contains("Duplicate entry")){
            return R.error("数据主键重复异常,请检查id等字段是否重复输入");
        }

        /*登录验证失败*/
        if(exception.getMessage().equals("NOLOGIN")){
            return R.error("用户未登录", 2);
        }

        return R.error(exception.getMessage());
    }

    /*异常处理器，拦截运行时异常*/
    @ExceptionHandler(RuntimeException.class)
    public R<String> ExceptionHandler(RuntimeException exception){
        log.error("拦截到企业级异常:" + exception.getMessage());

        /*数据库主键重复异常*/
        if(exception.getMessage().contains("Duplicate entry") && exception.getMessage().contains("userEmail")){
            return R.error("该邮箱已注册账号");
        }
        if(exception.getMessage().contains("Duplicate entry") && exception.getMessage().contains("site.urlAddres")){
            return R.error("网站链接已经存在");
        }
        if(exception.getMessage().contains("Duplicate entry")){
            return R.error("数据主键重复异常,请检查id等字段是否重复输入");
        }
        if(exception.getMessage().contains("smtp.qq.com")){
            return R.error("邮件发送失败，服务器网络异常");
        }
        if(exception.getMessage().contains("Data too long")){
            return R.error("字段超出了规定的长度");
        }
        return R.error(exception.getMessage());
    }
}
