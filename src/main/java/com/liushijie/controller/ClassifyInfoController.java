package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liushijie.common.R;
import com.liushijie.entity.Airticle;
import com.liushijie.entity.Classifyinfo;
import com.liushijie.entity.User;
import com.liushijie.service.AirticleService;
import com.liushijie.service.ClassifyInfoService;
import com.liushijie.service.UserService;
import com.liushijie.service.impl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@CrossOrigin
@Transactional
@RestController
@RequestMapping("/classify")
public class ClassifyInfoController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassifyInfoService classifyInfoService;
    @Autowired
    private AirticleService airticleService;

    /**
     * 获取全部分类信息
     *
     * @return
     */
    @GetMapping("/get")
    public R list() {
        List<Classifyinfo> list = classifyInfoService.list();

        return R.success(list);
    }

    /**
     * 添加分类
     *
     * @param classifyinfo
     * @return
     */
    @PostMapping("/back/save")
    public R save(@RequestBody Classifyinfo classifyinfo, HttpServletRequest request) {
        if (classifyinfo == null || classifyinfo.getClassifyName() == null
                || classifyinfo.getClassifyIntroduce() == null || classifyinfo.getColor1() == null
                || classifyinfo.getColor2() == null) {
            return R.error("操作失败，信息参数不完整");
        }
        //首先判断用户类型
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        Long id = Long.valueOf(redisService.getString(token));
        //获取用户信息
        User user = userService.getById(id);
        if (user == null) {
            return R.error("错误，用户信息不存在");
        }
        if (user.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        /*分类信息查重*/
        LambdaQueryWrapper<Classifyinfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classifyinfo::getClassifyName, classifyinfo.getClassifyName());
        int count = classifyInfoService.count(queryWrapper);
        if (count > 0) {
            return R.error("添加分类失败，分类名称重复");
        }

        /*添加分类信息*/
        classifyInfoService.save(classifyinfo);

        return R.success("分类添加成功");
    }

    /**
     * 修改分类信息
     * @param classifyinfo
     * @param request
     * @return
     */
    @PutMapping("/back/update")
    public R update(@RequestBody Classifyinfo classifyinfo, HttpServletRequest request){
        if (classifyinfo == null || classifyinfo.getClassifyName() == null
                || classifyinfo.getClassifyIntroduce() == null || classifyinfo.getColor1() == null
                || classifyinfo.getColor2() == null) {
            return R.error("操作失败，信息参数不完整");
        }
        //首先判断用户类型
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        Long id = Long.valueOf(redisService.getString(token));
        //获取用户信息
        User user = userService.getById(id);
        if (user == null) {
            return R.error("错误，用户信息不存在");
        }
        if (user.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        /*更新分类信息*/
        classifyInfoService.updateById(classifyinfo);
        return R.success("分类信息修改修改");
    }

    /**
     * 删除分类信息
     * @param
     * @param request
     * @return
     */
    @DeleteMapping("/back/delete")
    public R update(Long id, HttpServletRequest request){
        if (id == null || id == 0) {
            return R.error("操作失败，信息参数不完整");
        }
        //首先判断用户类型
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        Long userId = Long.valueOf(redisService.getString(token));
        //获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("错误，用户信息不存在");
        }
        if (user.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        /*判断分类是否存在文章*/
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Airticle::getArticleClassifyId, id);
        int count = airticleService.count(queryWrapper);
        if(count > 0){
            return R.error("删除分类失败，存在与该分类关联的文章");
        }

        /*更新分类信息*/
        classifyInfoService.removeById(id);
        return R.success("删除分类成功");
    }

    @GetMapping("/get/{id}")
    public R getOne(@PathVariable Long id){
        if(id == null || id == 0){
            return R.error("操作失败，参数信息有误");
        }

        Classifyinfo classify = classifyInfoService.getById(id);
        if (classify == null){
            return R.error("未查询到对应id的分类信息");
        }

        return R.success(classify);
    }
}

