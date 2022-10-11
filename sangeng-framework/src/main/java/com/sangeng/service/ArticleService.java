package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.ArticleVo;
import com.sangeng.domain.vo.PageVo;

public interface ArticleService extends IService<Article> {
    /**
     * 3.1查询热门文章，封装成ResponseResult返回
     * @return
     */
    ResponseResult hotArticleList();

    /**
     * 3.3 分页查询文章列表
     */
    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    /**
     * 写博文
     * @param article
     * @return
     */
    ResponseResult add(AddArticleDto article);

    /**
     * 5.11 文章列表
     */
    PageVo selectArticlePage(Article article, Integer pageNum, Integer pageSize);

    ArticleVo getInfo(Long id);

    void edit(ArticleDto articleDto);
}
