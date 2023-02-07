package com.liushijie.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liushijie.common.CustomException;
import com.liushijie.common.R;
import com.liushijie.entity.*;
import com.liushijie.qo.BackSearchArticleQo;
import com.liushijie.qo.SearchAirticleQo;
import com.liushijie.service.*;
import com.liushijie.service.impl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@CrossOrigin
@Transactional
@RestController
@RequestMapping("/airticle")
public class AirticleController {
    @Autowired
    private AirticleService airticleService;
    @Autowired
    private SystemSetupService systemSetupService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassifyInfoService classifyInfoService;
    @Autowired
    private CommentService commentService;
    /**
     * 获取全部文章
     *
     * @return
     */
    @GetMapping
    public R getALL() {
        List<Airticle> airticleList = airticleService.list();
        return R.success(airticleList);
    }

    /**
     * 获取全部文章
     * 获取全部文章的id和标题，主要用于后台系统设置
     * @return
     */
    @GetMapping("/getAll")
    public R getALLOnly() {
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Airticle::getId, Airticle::getArticleTitle, Airticle::getArticleClassifyId);
//        只能设置展示已通过审核和公开的文章
        queryWrapper.eq(Airticle::getArticlePass, 1).eq(Airticle::getArticleState, 1);
        List<Airticle> airticleList = airticleService.list(queryWrapper);
        return R.success(airticleList);
    }

    /**
     * 获取多个指定id的文章
     *
     * @return
     */
    @GetMapping("/ids")
    public R getIdsAirticle(Long[] ids) {
        if (ids.length < 1) {
            return R.error("文章查询失败，传递的id参数为空");
        }
        LambdaQueryWrapper<Airticle> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(Airticle::getId, ids)
                .eq(Airticle::getArticleState, 1).eq(Airticle::getArticlePass, 1);
        List<Airticle> list = airticleService.list(queryWrapper2);
        return R.success(list);
    }

    /**
     * 获取本站文章数量
     *
     * @return
     */
    @GetMapping("/counts")
    public R getAllCounts() {
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        /*只会搜索公开的和审核通过文章*/
        queryWrapper.eq(Airticle::getArticleState, 1).eq(Airticle::getArticlePass, 1);
        int count = airticleService.count(queryWrapper);
        return R.success(count);
    }

    /**
     * 分页条件搜索公开文章
     *
     * @param searchAirticleQo 分页搜索qo类
     * @return
     */
    @PostMapping("/search")
    public R getAirticleBySearch(@Valid @RequestBody SearchAirticleQo searchAirticleQo) {
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        if (searchAirticleQo.getType2() == 1) {
            /*是否被标题内容包含*/
            queryWrapper.like(Airticle::getArticleTitle, searchAirticleQo.getKey());
        } else if (searchAirticleQo.getType2() == 2) {
            /*匹配分类*/
            queryWrapper.eq(Airticle::getArticleClassifyName, searchAirticleQo.getKey());
        }
        /*只会搜索公开的和审核通过文章*/
        queryWrapper.eq(Airticle::getArticleState, 1).eq(Airticle::getArticlePass, 1);
        /*定义排序类型*/
        if (searchAirticleQo.getType() == 1) {
            queryWrapper.orderByDesc(Airticle::getPublishTime);
        } else if (searchAirticleQo.getType() == 2) {
            queryWrapper.orderByDesc(Airticle::getClick);
        } else if (searchAirticleQo.getType() == 3) {
            queryWrapper.orderByDesc(Airticle::getReview);
        } else {
            throw new CustomException("文章搜索类型错误（1-3）");
        }

        /*定义分页查询器*/
        Page page = new Page<Airticle>(searchAirticleQo.getPage(), searchAirticleQo.getPageSize());
        /*执行分页条件查询*/
        Page page1 = airticleService.page(page, queryWrapper);
        return R.success(page1);
    }

    /**
     * 查询单篇文章
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getAirticleById(@PathVariable Long id) {
        /*根据id查询文章信息*/
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != 0, Airticle::getId, id)
                .eq(Airticle::getArticleState, 1).eq(Airticle::getArticlePass, 1);
        Airticle airticle = airticleService.getOne(queryWrapper);
        /*更新阅读量*/
        if (airticle != null) {
            //阅读量加一
            airticle.setClick(airticle.getClick() + 1);
        } else {
            /*返回文章不存在的错误信息*/
            return R.error("文章不存在", 404);
        }
        airticleService.update(airticle, queryWrapper);

        return R.success(airticle);
    }

    /**
     * 根据分类查询文章
     *
     * @param classifyName 分类名称
     * @return
     */
    @GetMapping("/classify")
    public R getAirticleByClarrify(String classifyName) {
        if (Strings.isEmpty(classifyName)) {
            return R.error("分类信息为空");
        }
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Airticle::getArticleClassifyName, JSON.parse(classifyName))
                .eq(Airticle::getArticleState, 1).eq(Airticle::getArticlePass, 1);
        List<Airticle> list = airticleService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 获取作者文章数量
     *
     * @return
     */
    @GetMapping("/counts/{id}")
    public R getAllCountsbyAuthor(@PathVariable Long id) {
        if (id == null) {
            return R.error("参数错误");
        }
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Airticle::getUserId, id)
                .eq(Airticle::getArticleState, 1);
        int count = airticleService.count(queryWrapper);
        return R.success(count);
    }

    /**
     * 发布文章
     *
     * @param airticle
     * @return
     */
    @PostMapping("/save")
    public R saveOne(@RequestBody Airticle airticle, HttpServletRequest request) {
        //首先获取token，判断用户类型
        String token = request.getHeader("Token");
        String id = redisService.getString(token);
        if (id != null) {
            User user = userService.getById(Long.valueOf(id));
            if (user.getUserType() == 0) {
                //管理员文章可以直接通过
                airticle.setArticlePass(1);

                //添加数量
                Classifyinfo classifyinfo = classifyInfoService.getById(airticle.getArticleClassifyId());
                if (classifyinfo != null) {
                    classifyinfo.setArticleNumber(classifyinfo.getArticleNumber() + 1);
                    classifyInfoService.updateById(classifyinfo);
                } else {
                    return R.error("添加文章的分类信息为空");
                }
            } else {
                //普通用户文章需要审核
                airticle.setArticlePass(0);
            }
            //保存到数据库
            airticleService.save(airticle);

            return R.success("文章信息添加成功");
        } else {
            return R.error("账户登录已过期", 2);
        }
    }

    //管理文章页获取文章(根据用户权限获取不同内容)
    @PostMapping("/back/get")
    public R backGetArticle(@Valid  @RequestBody BackSearchArticleQo searchArticleQo, HttpServletRequest request) {
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

        Page page1 = new Page(searchArticleQo.getPage(), searchArticleQo.getPageSize());
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        //按时间升序排序
        queryWrapper.orderByDesc(Airticle::getPublishTime);
        //根据用户类型分发不同内容
        if (user.getUserType() == 0) {
            //管理员可查询到所有人所有状态的文章
            if (searchArticleQo.getKey() != "") {
                queryWrapper.like(Airticle::getArticleTitle, searchArticleQo.getKey())
                        .like(Airticle::getArticleContent, searchArticleQo.getKey())
                        .like(Airticle::getArticleDase, searchArticleQo.getKey());
            }
            if (searchArticleQo.getState() != 3) {
                //根据审核状态查询
                queryWrapper.eq(Airticle::getArticlePass, searchArticleQo.getState());
            }

        } else {
            //            普通用户只能查询自己的文章
            if (searchArticleQo.getKey() != "") {
                queryWrapper.like(Airticle::getArticleTitle, searchArticleQo.getKey())
                        .like(Airticle::getArticleContent, searchArticleQo.getKey())
                        .like(Airticle::getArticleDase, searchArticleQo.getKey());
            }
            if (searchArticleQo.getState() != 3) {
                //                根据审核状态查询
                queryWrapper.eq(Airticle::getArticlePass, searchArticleQo.getState());
            }
            queryWrapper.eq(Airticle::getUserId, id);
        }

        airticleService.page(page1, queryWrapper);
        return R.success(page1);
    }

    //管理文章页修改文章(根据用户权限进行不同操作)
    @PutMapping("/back/update")
    public R updateById(@RequestBody Airticle airticle, HttpServletRequest request) {
        //首先判断用户是否有权限修改
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
        //查询原文章
        Airticle originalAirticle = airticleService.getById(airticle.getId());
        if (user.getUserType() == 0) {
            //管理员可以直接修改
            airticleService.updateById(airticle);
            return R.success("文章修改成功");
        } else {
            //判断用户是否为作者
            if (!user.getId().equals(originalAirticle.getUserId())) {
                return R.error("操作失败，权限不足");
            }
            //设置文章为待审核(修改部分属性不需要审核)
            if (!originalAirticle.getArticleTitle().equals(airticle.getArticleTitle()) ||
                    !originalAirticle.getArticleContent().equals(airticle.getArticleContent()) ||
                    !originalAirticle.getArticleDase().equals(airticle.getArticleDase()) ||
                    !originalAirticle.getArticleImg().equals(airticle.getArticleImg())) {
                airticle.setArticlePass(0);
                //更新分类文章数量
                if (originalAirticle.getArticlePass() == 1) {
                    //更新数量
                    Classifyinfo classifyinfo = classifyInfoService.getById(airticle.getArticleClassifyId());
                    if (classifyinfo != null) {
                        classifyinfo.setArticleNumber(classifyinfo.getArticleNumber() - 1);
                        classifyInfoService.updateById(classifyinfo);
                    } else {
                        return R.error("文章的分类信息为空");
                    }
                }
            }
            airticleService.updateById(airticle);
            return R.success("文章修改成功,已重新提交审核");
        }
    }

    /**
     * 审核文章
     *
     * @param request
     * @return
     */
    @PutMapping("/back/check")
    public R checkArticle(Long id, Integer state, HttpServletRequest request) {
        //        首先判断用户是否为管理员
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
        Airticle airticle = airticleService.getById(id);
        if (airticle == null) {
            return R.error("待操作文章不存在");
        }
        //根据情况判断是否需要更新分类数量
        if (airticle.getArticlePass() == 1 && state != 1) {
            //减少数量
            Classifyinfo classifyinfo = classifyInfoService.getById(airticle.getArticleClassifyId());
            if (classifyinfo != null) {
                classifyinfo.setArticleNumber(classifyinfo.getArticleNumber() - 1);
                classifyInfoService.updateById(classifyinfo);
            } else {
                return R.error("文章的分类信息为空");
            }
        }
        if (airticle.getArticlePass() != 1 && state == 1) {
            //添加数量
            Classifyinfo classifyinfo = classifyInfoService.getById(airticle.getArticleClassifyId());
            if (classifyinfo != null) {
                classifyinfo.setArticleNumber(classifyinfo.getArticleNumber() + 1);
                classifyInfoService.updateById(classifyinfo);
            } else {
                return R.error("文章的分类信息为空");
            }
        }
        airticle.setArticlePass(state);

        //保存审核信息
        airticleService.updateById(airticle);
        return R.success("操作成功");
    }

    /**
     * 删除单篇文章
     *
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/back/delete")
    public R removeOne(Long id, HttpServletRequest request) {
        // 首先判断用户是否有权限删除文章
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

        Airticle airticle = airticleService.getById(id);
        if (airticle == null) {
            return R.error("待删除文章不存在");
        }

        if (user.getUserType() != 0 && !user.getId().equals(airticle.getUserId())) {
            return R.error("操作失败，权限不足");
        }

        //更新分类数量
        if (airticle.getArticlePass() == 1) {
            //减少数量
            Classifyinfo classifyinfo = classifyInfoService.getById(airticle.getArticleClassifyId());
            if (classifyinfo != null) {
                classifyinfo.setArticleNumber(classifyinfo.getArticleNumber() - 1);
                classifyInfoService.updateById(classifyinfo);
            } else {
                return R.error("文章的分类信息为空");
            }
        }
        //删除该文章下的所有评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, id);
        commentService.remove(queryWrapper);

        //删除文章
        airticleService.removeById(id);
        return R.success("文章删除成功");
    }
}
