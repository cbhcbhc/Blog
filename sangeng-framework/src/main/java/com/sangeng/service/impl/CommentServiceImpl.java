package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Comment;
import com.sangeng.domain.vo.CommentVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.CommentMapper;
import com.sangeng.service.CommentService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 * @author makejava
 * @since 2022-09-28 07:54:30
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private UserService userService;


    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        /**
         * 3.10.4.1 不考虑子评论
         * @param articleId
         * @param pageNum
         * @param pageSize
         * @return
         */
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),
                Comment::getArticleId,articleId);
        //根评论 rootId为 -1
        queryWrapper.eq(Comment::getRootId,-1);
        //评论类型
        queryWrapper.eq(Comment::getType,commentType);

        //分页查询
        Page<Comment> page =new Page<>(pageNum,pageSize);
        page(page,queryWrapper);

        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        /**
         * 3.10.4.2 查询子评论
         */
        for (CommentVo commentVo : commentVoList) {
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            //赋值
            commentVo.setChildren(children);
        }
        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }


    /**
     * Comment集合 ---》CommentVo集合
     * @param list
     * @return
     */
    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历vo集合
        for (CommentVo commentVo : commentVos) {
            //1.通过createBy查询用户的昵称并赋值
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //2.通过toCommentUserId查询用户的昵称并赋值
            //3.如果toCommentUserId不为 -1 才进行查询(CommentUserId为回复目标评论id)
            if (commentVo.getToCommentUserId() != -1){//不是-1说明它是回复某一条评论的，其他说明是回复文章的
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }

    /**
     * 根据根评论的id查询对应子评论的集合
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper =new LambdaQueryWrapper<>();
        //一个记录的根rootId等于 另一个记录的id ,说明该评论是另外一个评论的子评论，子评论中de
        //ToCommentUserId()不为 -1，所以会把 例子"toCommentUserId": 1,"toCommentUserName": "sg333",展示出来
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByDesc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(comments);
        return commentVos;
    }



    /**
     * 3.11 发表评论接口
     * @param comment
     * @return
     */
    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //其他字段使用handler包下的MyMetaObjectHandler.java填充就好
        save(comment);
        return ResponseResult.okResult();
    }
}


