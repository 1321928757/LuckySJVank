package com.liushijie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liushijie.common.R;
import com.liushijie.entity.Airticle;
import com.liushijie.entity.Comment;
import com.liushijie.entity.User;
import com.liushijie.qo.GetPageCommentQo;
import com.liushijie.service.AirticleService;
import com.liushijie.service.CommentService;
import com.liushijie.service.UserService;
import com.liushijie.service.impl.RedisService;
import com.liushijie.utils.CommentUtil;
import com.liushijie.vo.BackCommentVo;
import com.liushijie.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin
@Transactional
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private AirticleService airticleService;

    /**
     * 提交评论
     *
     * @param comment
     * @return
     */
    @PostMapping
    public R saveComment(@RequestBody Comment comment) {
        if (comment == null || comment.getArticleId() == 0) {
            return R.error("添加失败。评论信息为空");
        }


        Airticle airticle = airticleService.getById(comment.getArticleId());
        /*判断文章是否开启了评论区*/
        if(airticle.getCommentState() == 0){
            return R.error("发表评论失败，作者关闭了评论区");
        }

        /*增加文章评论数*/
        airticle.setReview(airticle.getReview() + 1);
        airticleService.updateById(airticle);

        commentService.save(comment);
        return R.success("评论添加成功");
    }

    /**
     * 根据文章id分页查询评论
     * 我们这里把评论分为三种，父评论（原评论），子评论（原评论下方的评论），回复评论（子评论中的回复评论）
     *
     * @param
     * @return
     */
    @PostMapping("/getcomment")
    public R getCommentByArtId(@Valid @RequestBody GetPageCommentQo pageCommentQo) {
        Page<Comment> pageInfo = new Page<>(pageCommentQo.getPage(), pageCommentQo.getPageSize());
        Page<CommentVo> pageVo = new Page<>(pageCommentQo.getPage(), pageCommentQo.getPageSize());

        //分页查询父级评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        /*根据id查询评论*/
        queryWrapper.eq(Comment::getArticleId, pageCommentQo.getArticleId());
        /*查询父级评论*/
        queryWrapper.eq(Comment::getParentId, 0);
        /*根据置顶排序*/
        queryWrapper.orderByDesc(Comment::getSetTop);
        /*根据时间排序*/
        queryWrapper.orderByDesc(Comment::getCommentDate);
        commentService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, pageVo, "records");
        List<Comment> comments = pageInfo.getRecords(); //查询到的父级评论

        //查询本文章所有子级评论
        LambdaQueryWrapper<Comment> queryWrapperChild = new LambdaQueryWrapper<>();
        queryWrapperChild.ne(Comment::getParentId, 0);
        queryWrapperChild.orderByDesc(Comment::getCommentDate);
        queryWrapperChild.eq(Comment::getArticleId, pageCommentQo.getArticleId());
        List<Comment> commentChilds = commentService.list(queryWrapperChild);

        //合并父子评论
        List<Comment> commentAll = new ArrayList<>();
        commentAll.addAll(commentChilds);
        commentAll.addAll(comments);
        /*将comment携带user信息封装为Vo类*/
        List<CommentVo> commentVos = new ArrayList<CommentVo>();
        for (Comment comment : commentAll) {
            /*保存评论的基本信息*/
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment, commentVo);

            /*查询评论的用户信息*/
            User user = userService.getById(comment.getUserId());
            BeanUtils.copyProperties(user, commentVo, "id");//评论用户的基本信息
            commentVos.add(commentVo);
        }
        //将父评论与子评论组装成树状结构
        commentVos = CommentUtil.recursionMethod(commentVos);
        pageVo.setRecords(commentVos);
        //计算评论总数
        pageVo.setCountId(String.valueOf(commentAll.size()));

        return R.success(pageVo);
    }

    /**
     * 删除评论
     * @param commentId 待删除的评论Id
     * @return
     */
    @DeleteMapping("/{commentId}")
    public R removeComment(@PathVariable Long commentId, HttpServletRequest request) {
        if (commentId == null || commentId == 0) {
            R.error("操作失败，评论id不存在");
        }
        /*首先判断用户是否有删除权限*/
        /*根据用户token查询到当前操作用户id，判断是否存在权限删除评论*/
        String token = request.getHeader("Token");
        Long id = Long.valueOf(redisService.getString(token));
        User user = userService.getById(id);
        Comment comment = commentService.getById(commentId);
        /*查询本条评论所属文章*/
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Airticle::getId, comment.getArticleId());
        Airticle airticle = airticleService.getOne(queryWrapper);

        /*查询本评论文章下的子评论*/
        LambdaQueryWrapper<Comment> queryWrapperChild = new LambdaQueryWrapper<>();
        queryWrapperChild.eq(Comment::getArticleId, airticle.getId())
                .ne(Comment::getParentId, 0);
        List<Comment> commentChilds = commentService.list(queryWrapperChild);

        /*获取与待删除评论有关的子评论id*/
        ArrayList<Long> childIds = CommentUtil.getConnectCommentId(commentId, commentChilds);
        if (comment == null) {
            return R.error("待删除评论已不存在");
        }
        /*以下情况可以删除评论*/
        //*1.管理员 2.评论作者自己 3.本篇文章作者*
        if (user.getUserType() == 0 || comment.getUserId().equals(id) || airticle.getUserId().equals(id)) {
            commentService.removeByIds(childIds);

            /*更新文章评论数*/
            LambdaQueryWrapper<Comment> QueryWrapper1 = new LambdaQueryWrapper<>();
            QueryWrapper1.eq(Comment::getArticleId, comment.getArticleId());
            int count = commentService.count(QueryWrapper1);
            airticle.setReview(count);
            airticleService.updateById(airticle);

            return R.success("评论删除成功");
        }
        return R.error("您无权操作");
    }

    /**
     * 置顶（取消置顶）文章
     * @param commentId 待置顶评论id
     * @param topState  置顶状态
     * @return
     */
    @PutMapping("/settop/{commentId}/{topState}")
    public R setTopComment(@PathVariable Long commentId, @PathVariable Integer topState, HttpServletRequest request) {
        if (commentId == null || commentId == 0) {
            R.error("操作失败，评论id不存在");
        }
        if (topState != 0 && topState != 1) {
            R.error("操作失败，置顶参数有误");
        }
        /*首先判断用户是否有置顶权限*/
        /*根据用户token查询到当前操作用户id，判断是否存在权限删除评论*/
        String token = request.getHeader("Token");
        Long id = Long.valueOf(redisService.getString(token));
        User user = userService.getById(id);
        Comment comment = commentService.getById(commentId);
        /*查询本条评论所属文章*/
        LambdaQueryWrapper<Airticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Airticle::getId, comment.getArticleId());
        Airticle airticle = airticleService.getOne(queryWrapper);

        if (comment == null) {
            return R.error("待置顶评论已不存在");
        }
        /*只有文章的作者可以置顶自己文章内的评论*/
        if (airticle.getUserId() == id) {
            comment.setSetTop(topState);
            commentService.updateById(comment);
            return R.success("评论状态修改成功");
        }
        return R.error("您无权操作");
    }

    /**
     * 查询评论数量
     * @return
     */
    @GetMapping("/count")
    public R getUserCounts(){
        int count = commentService.count();
        return R.success(count);
    }

    /**
     * 后台获取评论
     * @param request
     * @return
     */
    @GetMapping("/back/get")
    public R backGetComment(String key, Integer page, Integer pageSize, HttpServletRequest request){
        if(page == null || pageSize == null){
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

        List<Comment> comments = new ArrayList<>();
        Page<Comment> page1 = new Page<>(page, pageSize);
        Page<BackCommentVo> pageVo = new Page<>(page, pageSize);
        /*根据用户类型回显不同数据*/
        if(user.getUserType() == 0){
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Strings.isNotEmpty(key), Comment::getContent, key);
            queryWrapper.orderByDesc(Comment::getCommentDate);
            commentService.page(page1, queryWrapper);
        }else{
            /*普通用户查询自己的评论和自己文章的评论*/
            /*查询用户的所有文章的id*/
            LambdaQueryWrapper<Airticle> queryWrapperAirticle = new LambdaQueryWrapper<>();
            queryWrapperAirticle.eq(Airticle::getUserId, user.getId());
            List<Airticle> artList = airticleService.list(queryWrapperAirticle);
            List<Long> artIds = new ArrayList<>();
            for (Airticle airticle : artList) {
                artIds.add(airticle.getId());
            }

            LambdaQueryWrapper<Comment> queryWrapperComment = new LambdaQueryWrapper<>();
            if(artIds.size() > 0){
                /*用户文章数不为0*/
                queryWrapperComment.eq(Comment::getUserId, user.getId()).or()
                        .in(Comment::getArticleId, artIds)
                        .like(Strings.isNotEmpty(key), Comment::getContent, key);;
            }else{
                queryWrapperComment.eq(Comment::getUserId, user.getId())
                        .like(Strings.isNotEmpty(key), Comment::getContent, key);
            }

            commentService.page(page1, queryWrapperComment);
        }

        BeanUtils.copyProperties(page1, pageVo, "records");
        comments = page1.getRecords();
        /*将评论封装为Vo类*/
        List<BackCommentVo> commentVos = new ArrayList<>();
        for (Comment comment : comments) {
            BackCommentVo commentVo = new BackCommentVo();
            BeanUtils.copyProperties(comment, commentVo);

            /*查询该评论的用户名*/
            User commentUser = userService.getById(comment.getUserId());
            commentVo.setUsername(commentUser.getUsername());
            /*查询该评论所属的文章名*/
            Airticle commentAirticle = airticleService.getById(comment.getArticleId());
            commentVo.setAirticleName( commentAirticle.getArticleTitle());
            /*查询父级评论的作者名和评论内容*/
            if(comment.getParentId() != 0){
                Comment commentParentComment = commentService.getById(comment.getParentId());
                User commentParentUser = userService.getById(commentParentComment.getUserId());
                commentVo.setParentName(commentParentUser.getUsername());
                commentVo.setParentContent(commentParentComment.getContent());
            }else{
                commentVo.setParentContent("");
                commentVo.setParentName("");
            }

            commentVos.add(commentVo);
        }
        pageVo.setRecords(commentVos);

        return R.success(pageVo);
    }

}
