package com.liushijie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.Message;
import com.liushijie.mapper.MessageMapper;
import com.liushijie.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper,Message> implements MessageService {
}
