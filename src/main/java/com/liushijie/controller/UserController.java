package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liushijie.common.R;
import com.liushijie.entity.Airticle;
import com.liushijie.entity.Classifyinfo;
import com.liushijie.entity.Comment;
import com.liushijie.entity.User;
import com.liushijie.qo.*;
import com.liushijie.service.AirticleService;
import com.liushijie.service.ClassifyInfoService;
import com.liushijie.service.CommentService;
import com.liushijie.service.UserService;
import com.liushijie.service.impl.RedisService;
import com.liushijie.utils.EmailUtils;
import com.liushijie.vo.LoginSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户类控制层
 */
@Slf4j
@CrossOrigin
@Transactional
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private AirticleService airticleService;
    @Autowired
    private ClassifyInfoService classifyInfoService;

    /**
     * 查找用户是否存在
     *
     * @param user
     * @return
     */
    @GetMapping("/seek")
    public R seek(User user) {
        /*在数据库中查询账号是否存在*/
        User user1 = userService.seekUser(user.getUsername());

        if (user1 != null) {
            /*保存账号基本消息*/
            user1.setPassword("");
            return R.success(user1);
        } else {
            return R.error("用户不存在");
        }
    }

    /**
     * 用户登录
     *
     * @param
     * @return
     */
    @PostMapping("/login")
    public R login(@Valid  @RequestBody UserLoginQo userLoginQo) {
        /*根据uuid从缓存中拿取验证码，校验输入的验证码*/
        String code = redisService.getString(userLoginQo.getCodeUuid());
        if (code == null) {
            return R.error("验证码已过期");
        }
        if (!code.toUpperCase().equals(userLoginQo.getVericode().toUpperCase())) {
            return R.error("验证码错误");
        }
        /*寻找用户*/
        User user = userService.seekUser(userLoginQo.getUsername());
        if (user == null) {
            return R.error("用户不存在");
        }
        /*验证码密码*/
        if (!user.getPassword().equals(userLoginQo.getPassword())) {
            return R.error("用户密码错误");
        }

        /*生成用户验证token*/
        UUID token = UUID.randomUUID();
        /*将token存入缓存中,有效期为三天，这里我们把token当成key,用户权限当成value*/
        redisService.setStringTime(token.toString(), user.getId().toString(), new Long(3), TimeUnit.DAYS);

        /*删除本次登录验证码的缓存*/
        redisService.delete(userLoginQo.getCodeUuid());

        /*返回登录成功的信息*/
        LoginSuccessVo loginVo = new LoginSuccessVo();
        user.setPassword("");
        loginVo.setUser(user);
        loginVo.setToken(token.toString());
        return R.success(loginVo);
    }

    /**
     * 用户注册
     *
     * @return
     */
    @PostMapping("/register")
    public R register(@Valid  @RequestBody UserRegisterQo registerQo) {
        /*根据邮箱从缓存中取出对应验证码，进行验证码校验*/
        String code = redisService.getString(registerQo.getUserEmail());

        /*校验验证码*/
        if (!registerQo.getVericode().equals(code)) {
            return R.error("验证码错误");
        }
        /*校验邮箱和用户名是否已经存在*/
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getUserEmail, registerQo.getUserEmail());
        User user1 = userService.getOne(queryWrapper1);
        if (user1 != null) {
            return R.error("该邮箱已经被注册");
        }
        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getUsername, registerQo.getUsername());
        User user2 = userService.getOne(queryWrapper2);
        if (user1 != null) {
            return R.error("该用户名已经被注册");
        }

        /*成功注册，添加注册用户*/
        User user3 = new User();
        BeanUtils.copyProperties(registerQo, user3);
        userService.save(user3);

        /*删除本次注册的验证码缓存*/
        redisService.delete(registerQo.getUserEmail());

        return R.success("用户注册成功");
    }

    /**
     * 忘记密码,重置密码
     */
    @PutMapping("/updatePwd")
    public R updatePassword(@Valid  @RequestBody UserResetQo resetQo) {
        String token = resetQo.getToken();
        /*校验token是否存在*/
        if (token != null) {
            if (redisService.getString(token) == null) {
                return R.error("身份校验已过期，请重新校验，在两分钟内完成操作");
            }
        } else {
            return R.error("身份校验失败，请按步骤操作");
        }

        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getUserEmail, resetQo.getUserEmail());
        User user = new User();
        user.setPassword(resetQo.getPassword());
        userService.update(user, queryWrapper1);

        return R.success("密码修改成功");
    }

    /**
     * 查询用户数量
     *
     * @return
     */
    @GetMapping("/count")
    public R getUserCounts() {
        int count = userService.count();
        return R.success(count);
    }

    /**
     * 前台根据id查询用户信息
     * @return
     */
    @GetMapping("/get")
    public R getUserInfo(Long id) {
        if(id == null){
             return R.error("操作失败【，信息参数不完整");
        }
        User user = userService.getById(id);
        user.setPassword("");
        return R.success(user);
    }

    /**
     * 后台获取用户信息
     */
    @GetMapping("/back/get")
    public R backGetUserAll(String key, Integer page, Integer pageSize, HttpServletRequest request) {
        if (page == null || pageSize == null) {
            return R.error("操作失败，参数信息有误");
        }
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("错误，用户信息不存在");
        }

        Page<User> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(User::getUserType).orderByDesc(User::getCreateTime);
        queryWrapper.like(Strings.isNotEmpty(key), User::getUsername, key).or()
                .like(Strings.isNotEmpty(key), User::getUserSignature, key);
        /*获取全部用户信息*/
        userService.page(page1, queryWrapper);

        return R.success(page1);
    }

    /**
     * 后台删除用户
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/back/remove")
    public R removeOneById(Long id, HttpServletRequest request) {
        if (id == null) {
            return R.error("操作失败，参数信息有误");
        }
        /*登录校验*/
        String token = request.getHeader("Token");
        if (Strings.isEmpty(token)) {
            return R.error("登录信息校验失败");
        }

        //获取当前执行操作用户信息
        Long userId = Long.valueOf(redisService.getString(token));
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("错误，用户信息不存在");
        }
        /*只有账户主人（注销）和管理员可以删除账户*/
        if (!user.getId().equals(id) && user.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        /*删除该用户所有的评论*/
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getUserId, id);
        commentService.remove(commentLambdaQueryWrapper);
        /*删除文章前更新分类文章数量*/
        LambdaQueryWrapper<Airticle> airticleCountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        airticleCountLambdaQueryWrapper.eq(Airticle::getArticlePass, 1)
                .eq(Airticle::getUserId, id);
        List<Airticle> list = airticleService.list(airticleCountLambdaQueryWrapper);
        for (Airticle airticle : list) {
            Classifyinfo classify = classifyInfoService.getById(airticle.getArticleClassifyId());
            classify.setArticleNumber(classify.getArticleNumber() - 1);
            classifyInfoService.updateById(classify);
        }
        /*删除用户所有的文章*/
        LambdaQueryWrapper<Airticle> airticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        airticleLambdaQueryWrapper.eq(Airticle::getUserId, id);
        airticleService.remove(airticleLambdaQueryWrapper);

        /*删除用户信息*/
        userService.removeById(id);
        return R.success("用户删除成功");
    }

    /**
     * 根据id修改用户
     *
     * @param user
     * @param request
     * @return
     */
    @PutMapping("/back/update")
    public R update(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
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
        /*只有账户主人和管理员可以修改账户*/
        if (!currentUser.getId().equals(user.getId()) && currentUser.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }
        if (currentUser.getId().equals(user.getId()) && user.getUserType() == 0 && currentUser.getUserType() == 1) {
            return R.error("操作失败，权限不足（哈哈哈，是不是出于好奇把自己管理员账户撤了，请联系管理员）");
        }
        /*如果密码为空就设置为null（让MP不更新密码）*/
        if(Strings.isEmpty(user.getPassword())){
            user.setPassword(null);
        }

        /*修改用户信息*/
        userService.updateById(user);
        return R.success("用户信息修改成功");
    }

    /**
     * 后台根据id查询用户（详细查询）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/back/getone")
    public R getUserById(Long id, HttpServletRequest request) {
        if (id == null) {
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
        /*只有账户主人和管理员可以查询到账户的详细信息*/
        if (!currentUser.getId().equals(id) && currentUser.getUserType() != 0) {
            return R.error("操作失败，权限不足");
        }

        User userInfo = userService.getById(id);
        return R.success(userInfo);
    }

    /**
     * 后台个人修改邮箱
     *
     * @param emailResetQo
     * @return
     */
    @PutMapping("/back/reset/email")
    public R resetEmail(@Valid  @RequestBody EmailResetQo emailResetQo) {
        /*判断旧邮箱验证是否成功*/
        String token = redisService.getString(emailResetQo.getToken());
        if (token == null) {
            return R.error("旧邮箱身份校验已过期，请重新验证");
        }

        /*新邮箱验证*/
        String code = redisService.getString(emailResetQo.getUserEmail());
        if (code == null) {
            return R.error("新邮箱验证码已过期，请重新验证");
        }
        /*对比验证码*/
        if (!code.equals(emailResetQo.getVericode())) {
            return R.error("验证码错误");
        }

        /*更改邮箱*/
        User user = userService.getById(emailResetQo.getId());
        if (user == null) {
            return R.error("操作失败，待操作用户信息不存在");
        }
        user.setUserEmail(emailResetQo.getUserEmail());
        userService.updateById(user);
        return R.success("邮箱修改成功");
    }

    @PutMapping("/back/reset/pwd")
    public R resetPassword(@Valid  @RequestBody PwdResetQo pwdResetQo, HttpServletRequest request) {
        if (pwdResetQo == null) {
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
        /*只有账户主人和管理员可以查询到账户的详细信息*/
        if (!currentUser.getId().equals(pwdResetQo.getId())) {
            return R.error("操作失败，权限不足");
        }

        /*校验旧密码*/
        if (!currentUser.getPassword().equals(pwdResetQo.getOldPassword())) {
            return R.error("原密码错误,请重新输入");
        }
        /*修改密码*/
        currentUser.setPassword(pwdResetQo.getNewPassword());
        userService.updateById(currentUser);
        return R.success("密码修改成功");
    }

}
