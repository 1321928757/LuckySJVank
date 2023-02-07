package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liushijie.common.R;
import com.liushijie.entity.Message;
import com.liushijie.qo.SaveMessageQo;
import com.liushijie.service.MessageService;
import com.liushijie.service.impl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisService redisService;
    /**
     * 分页查询数据
     * @return
     */
    @GetMapping
    public R list(Integer page, Integer pageSize){
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Message::getMessageDate);
        Page<Message> page1 = new Page<>(page, pageSize);
        Page<Message> page2 = messageService.page(page1, queryWrapper);
        return R.success(page2);
    }

    @PostMapping
    public R save(@Valid @RequestBody SaveMessageQo saveMessageQo){
        /*验证码校验*/
        String code = redisService.getString(saveMessageQo.getUuid());
        if(code == null){
            return R.error("留言失败，验证码已过期");
        }
        if(!code.toUpperCase().equals(saveMessageQo.getCode().toUpperCase())){
            return R.error("留言失败，验证码错误");
        }

        /*保存留言消息*/
        Message message = new Message();
        BeanUtils.copyProperties(saveMessageQo, message);
        messageService.save(message);

        /*删除验证码缓存*/
        redisService.delete(saveMessageQo.getUuid());

        return R.success("留言成功");
    }
}
