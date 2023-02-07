package com.liushijie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.Systemsetup;
import com.liushijie.mapper.SystemSetupMapper;
import com.liushijie.service.SystemSetupService;
import org.springframework.stereotype.Service;

@Service
public class SystemSetupServiceImpl extends ServiceImpl<SystemSetupMapper, Systemsetup> implements SystemSetupService {
}
