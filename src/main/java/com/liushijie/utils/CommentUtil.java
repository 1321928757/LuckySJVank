package com.liushijie.utils;


import com.liushijie.entity.Comment;
import com.liushijie.vo.CommentVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论工具
 * @author LiuShiJie
 * @date 2022-12-21 12:19
 */
public class CommentUtil {
    /**
     * 递归方法转换成树形结构集合
     * @param treeList
     * @return
     */
    public static List<CommentVo> recursionMethod(List<CommentVo> treeList) {
        List<CommentVo> trees = new ArrayList<>();
        for (CommentVo tree : treeList) {
            // 找出父节点
            if (0 == tree.getParentId()) {
                // 调用递归方法填充子节点列表
                trees.add(findChildren(tree, treeList));
            }
        }
        return trees;
    }

    /**
     * 递归方法
     * @param tree 父节点对象
     * @param treeList 所有的List
     * @return
     */
    public static CommentVo findChildren(CommentVo tree, List<CommentVo> treeList) {
        for (CommentVo node : treeList) {
            if (tree.getId().equals(node.getParentId())) {
                // 递归 调用自身
                tree.getChild().add(findChildren(node, treeList));
            }
        }
        return tree;
    }

    /**
     * 获取与输入id有关的所有子评论id
     * @param commentId 父评论id
     * @param allComments 文章子评论集合
     * @return
     */
    public static ArrayList<Long> getConnectCommentId(Long commentId, List<Comment> allComments){
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(commentId);
        for (Comment comment : allComments) {
            if(comment.getParentId() == 0) continue;
            if(comment.getParentId().equals(commentId)){
                ids.add(comment.getId());
                continue;
            }
            Long id = findChildrenId(commentId, comment.getParentId(), comment.getId(), allComments);
            //父评论为其他的，向上搜索
            if(id != 0){
                ids.add(id);
            }
        }

        return ids;
    }

    /**
     * 递归方法
     * @param
     * @param
     * @return
     */
    public static Long findChildrenId(Long commentId, Long parentId, Long childId, List<Comment> allComments) {
        for (Comment comment : allComments) {
            if(comment.getId().equals(parentId)){
                if(comment.getParentId().equals(commentId)){
                    return childId;
                }else if(comment.getParentId() != 0){
                    findChildrenId(commentId, comment.getParentId(), childId, allComments);
                }
                break;
            }
        }
        return new Long(0);
    }
}
