package com.liushijie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.Classifyinfo;
import com.liushijie.mapper.ClassifyMapper;
import com.liushijie.service.ClassifyInfoService;
import org.springframework.stereotype.Service;

@Service
public class ClassifyInfoServiceImpl extends ServiceImpl<ClassifyMapper, Classifyinfo> implements ClassifyInfoService{
}
