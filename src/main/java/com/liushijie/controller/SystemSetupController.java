package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liushijie.common.R;
import com.liushijie.entity.Systemsetup;
import com.liushijie.entity.User;
import com.liushijie.service.SystemSetupService;
import com.liushijie.service.UserService;
import com.liushijie.service.impl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/setup")
public class SystemSetupController {
    @Autowired
    private SystemSetupService systemSetupService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    /**
     * 获取当前系统设置
     * @return
     */
    @GetMapping("/get")
    public R getSetUp(){
        LambdaQueryWrapper<Systemsetup> queryWrapper = new LambdaQueryWrapper<>();
        Systemsetup setup = systemSetupService.getOne(queryWrapper);
        return R.success(setup);
    }

    @PutMapping("/back")
    public R saveSetUp(@RequestBody Systemsetup systemsetup, HttpServletRequest request){
        if (systemsetup == null) {
            return R.error("操作失败，参数信息有误");
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
        /*只有管理员可以修改系统设置*/
        if (currentUser.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        /*修改系统设置*/
        systemSetupService.updateById(systemsetup);

        return R.success("系统设置修改成功");
    }
}
