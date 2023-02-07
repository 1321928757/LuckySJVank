package com.liushijie.controller;

import com.liushijie.common.R;
import com.liushijie.qo.EmailCodeQo;
import com.liushijie.qo.ImgCodeQo;
import com.liushijie.service.impl.RedisService;
import com.liushijie.utils.EmailUtils;
import com.liushijie.utils.RandUtils;
import com.liushijie.vo.ImgCodeVo;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码controller
 */
@CrossOrigin
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private EmailUtils emailUtils;

    @GetMapping("/send/png")
    public R png() {
        // png类型
        SpecCaptcha captcha = new SpecCaptcha(100, 40, 4);
        //转为base64格式
        String img = captcha.toBase64();
        // 获取验证码的字符
        String text = captcha.text();
        //生成uuid,存储uuid和验证码到缓存,验证码有效时间为2分钟
        UUID uuid = UUID.randomUUID();
        redisService.setStringTime(uuid.toString(), text, new Long(2), TimeUnit.MINUTES);

        // 输出验证码
//        captcha.out(response.getOutputStream());
        ImgCodeVo imgAndCode = new ImgCodeVo();
        imgAndCode.setCodeImg(img);
        imgAndCode.setUuid(uuid);

        return R.success(imgAndCode);
    }

    /**
     * 发送邮箱注册验证码
     *
     * @param email
     * @return
     */
    @GetMapping("/send/{email}")
    public R sendVeriCOde(@PathVariable String email, HttpSession session) {
        if (email == null || email == "") {
            return R.error("验证码发送失败，邮箱信息为空");
        }
        /*检查一分钟内是否已经发送过验证码*/
        if(redisService.getExpire(email, TimeUnit.MINUTES) - 3 > 0){
            return R.error("邮箱验证码发送失败，同一邮箱一分钟内只允许发送一次验证码");
        }
        /*生成验证码*/
        String veriCode = RandUtils.getRandomCode(4);
        /*发送邮件*/
        String content = "您的邮箱验证码为： " + veriCode + "  ,该验证码在五分钟内有效，请勿将消息泄露给他人，以免造成账号丢失等问题";
        String title = "LuckySJ博客邮箱验证码";
        emailUtils.sendCode(email, content, title);

        /*将邮箱-验证码存入缓存中，时效性五分钟*/
        redisService.setStringTime(email, veriCode, new Long(5), TimeUnit.MINUTES);

        return R.success("验证码发送成功");
    }

    /**
     * 图片验证码单个校验
     * @return
     */
    @PostMapping("/confirm/img")
    public R imgCodeVerify(@Valid  @RequestBody ImgCodeQo codeQo){
        System.out.println(codeQo);
        /*根据key从缓存中拿取验证码*/
        String code = redisService.getString(codeQo.getCodeUuid());

        if(code == null){
            return R.error("验证码已过期");
        }
        /*验证码校验*/
        if(!codeQo.getVericode().toUpperCase().equals(code.toUpperCase())){
            return R.error("验证码错误");
        }
        return R.success("验证码校验通过");
    }

    /**
     * 邮箱验证码单个校验（返回临时token，用于修改密码等敏感操作）
     * @return
     */
    @PostMapping("/confirm/email")
    public R emailCodeVerify(@Valid @RequestBody EmailCodeQo codeQo){
        /*根据key从缓存中拿取验证码*/
        String code = redisService.getString(codeQo.getUserEmail());
        if(code == null){
            return R.error("验证码已过期");
        }
        /*验证码校验*/
        if(!codeQo.getVericode().toUpperCase().equals(code.toUpperCase())){
            return R.error("验证码错误");
        }

        /*数据中返回一个临时token(生存时间为2min),用于重置密码或者重置邮箱的身份校验*/
        UUID token = UUID.randomUUID();
        redisService.setStringTime(token.toString(), "", new Long(2), TimeUnit.MINUTES);

        return R.success(token);
    }


}
