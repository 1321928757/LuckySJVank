package com.liushijie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liushijie.entity.Site;
import com.liushijie.mapper.SiteMapper;
import com.liushijie.service.SiteService;
import org.springframework.stereotype.Service;

@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {
}
