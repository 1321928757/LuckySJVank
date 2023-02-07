package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liushijie.common.R;
import com.liushijie.entity.Site;
import com.liushijie.entity.User;
import com.liushijie.qo.ApplySiteQo;
import com.liushijie.qo.BackSearchArticleQo;
import com.liushijie.service.SiteService;
import com.liushijie.service.UserService;
import com.liushijie.service.impl.RedisService;
import com.liushijie.utils.EmailUtils;
import com.liushijie.utils.RandUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/site")
public class SiteController {
    @Autowired
    private SiteService siteService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtils emailUtils;
    /**
     * 前台获取对应类型的网站信息
     * @param type 网站类型：1友情链接 2推荐网站 3全部网站
     * @return
     */
    @GetMapping
    public R list(Integer type){
        LambdaQueryWrapper<Site> queryWrapper = new LambdaQueryWrapper<>();
        /*首先判断类型是否符合要求*/
        if(type == 1){
            queryWrapper.eq(Site::getUrlType, 1).eq(Site::getUrlPass, 1);
        }else if(type == 2){
            queryWrapper.eq(Site::getUrlType, 2).eq(Site::getUrlPass, 1);
        }else{
            return R.error("数据获取失败，参数有误");
        }
        List<Site> list = siteService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/apply")
    public R apllySite(@Valid @RequestBody ApplySiteQo applySiteQo){
        /*判断网站是否已经存在*/
        LambdaQueryWrapper<Site> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Site::getUrlAddres, applySiteQo.getUrlAddres());
        int count = siteService.count(queryWrapper);
        if(count > 0){
            return R.error("操作失败，该网站链接已经存在或者正在申请中");
        }

        /*添加申请*/
        Site site = new Site();
        BeanUtils.copyProperties(applySiteQo, site);
        siteService.save(site);

        return R.success("网站申请成功");
    }

    @GetMapping("/get")
    public R getById(Long id){
        if(id == null){
            return R.error("操作失败，参数信息有误");
        }
        Site site = siteService.getById(id);
        return R.success(site);
    }
    /**
     * 后台获取全部链接
     * @param searchQo
     * @param request
     * @return
     */
    @PostMapping("/back/get")
    public R getAllsite(@Valid  @RequestBody BackSearchArticleQo searchQo, HttpServletRequest request){
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取当前执行操作用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            return R.error("错误，当前操作用户信息不存在");
        }
        if(currentUser.getUserType() != 0){
            return R.error("操作失败，权限不足");
        }


        Page<Site> page = new Page<>(searchQo.getPage(), searchQo.getPageSize());
        LambdaQueryWrapper<Site> queryWrapper = new LambdaQueryWrapper<>();
        if(searchQo.getState() != 3){
            queryWrapper.eq(Site::getUrlPass, searchQo.getState())
                    .and(siteLambdaQueryWrapper -> siteLambdaQueryWrapper
                            .like(Site::getUrlName, searchQo.getKey()).or()
                    .like(Site::getUrlReferral, searchQo.getKey()).or()
                    .like(Site::getUrlAddres, searchQo.getKey()));
        }else{
            queryWrapper.like(Site::getUrlName, searchQo.getKey()).or()
                    .like(Site::getUrlReferral, searchQo.getKey()).or()
                    .like(Site::getUrlAddres, searchQo.getKey());
        }
        queryWrapper.orderByAsc(Site::getUrlType);
        siteService.page(page, queryWrapper);
        return R.success(page);
    }

    /**
     * 后台添加链接
     * @param siteQo
     * @param request
     * @return
     */
    @PostMapping("/back/save")
    public R save(@Valid  @RequestBody ApplySiteQo siteQo,  HttpServletRequest request){
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取当前执行操作用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            return R.error("错误，当前操作用户信息不存在");
        }
        if(currentUser.getUserType() != 0){
            return R.error("操作失败，权限不足");
        }

        Site site = new Site();
        BeanUtils.copyProperties(siteQo, site);
        site.setUrlPass(1);
        siteService.save(site);
        return R.success("链接添加成功");
    }

    @PutMapping("/back/update")
    public R update(@RequestBody Site site, HttpServletRequest request){
        if(site == null || site.getId() == null || site.getWebmasterEmail() == null
                ||site.getUrlLitimg() == null || site.getUrlAddres() == null ||
                site.getUrlType() == null || site.getUrlReferral() == null ||site.getUrlName() == null){
            return R.error(" 操作失败，参数信息有误");
        }
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取当前执行操作用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            return R.error("错误，当前操作用户信息不存在");
        }
        if(currentUser.getUserType() != 0){
            return R.error("操作失败，权限不足");
        }

        /*查询原来的信息*/
        Site originSite = siteService.getById(site.getId());
        /*修改链接信息*/
        siteService.updateById(site);

        /*审核链接结束发送邮箱信息提示站长审核结果*/
        String content = "";
        if(originSite.getUrlPass() != 1 && site.getUrlPass() == 1){
            content = "亲爱的朋友您好！您在LuckySJ博客申请的链接[" + site.getUrlAddres() + "]已通过";
        }else if(originSite.getUrlPass() != 2 && site.getUrlPass() == 2){
            content = "亲爱的朋友您好！您在LuckySJ博客申请的链接[" + site.getUrlAddres() + "]未通过审核";
        }
        String title = "LuckySJ博客链接审核结果";
        emailUtils.sendCode(site.getWebmasterEmail(), content, title);

        return R.success("修改链接成功");
    }

    @DeleteMapping("/back/remove")
    public R removeById(Long id, HttpServletRequest request){
        if(id == null){
            return R.error(" 操作失败，参数信息有误");
        }
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取当前执行操作用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            return R.error("错误，当前操作用户信息不存在");
        }
        if(currentUser.getUserType() != 0){
            return R.error("操作失败，权限不足");
        }

        siteService.removeById(id);
        return R.success("删除链接成功");
    }
}
