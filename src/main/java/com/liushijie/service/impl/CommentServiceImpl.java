package com.liushijie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.Comment;
import com.liushijie.mapper.CommentMapper;
import com.liushijie.service.CommentService;
import org.springframework.stereotype.Service;

//评论业务层
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
