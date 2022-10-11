package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.*;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.ArticleTagService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;

import com.sangeng.utils.RedisCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private CategoryService categoryService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ArticleTagService articleTagService;


    /**
     * 3.1 查询热门文章，封装成ResponseResult返回
     * @return
     */
    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行降序排序
        queryWrapper.orderByDesc(Article::getViewCount);

        //最多只查询10条
        Page<Article> page = new Page<>(1,10);
        page(page, queryWrapper);
        //获取源数据记录(字段太多，有些不需要)
        List<Article> articles = page.getRecords();

        //bean拷贝(字段名字，字段类型需要保持一致，不然拷贝不了) 拷贝返回数据
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for (Article article : articles) {
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article,vo);
//            articleVos.add(vo);
//        }

        //使用工具类拷贝好的新数据
        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);
        return ResponseResult.okResult(vs);
    }

    /**
     * 3.3 分页查询文章列表
     */
    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        /**
         * 查询条件
         *  1.如果前端有传categoryId，查询时要和传入的相同
         *  2.状态是正式发布的
         *  3.对isTop进行排序
         */

        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //1.如果前端有传categoryId，查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId) && categoryId>0,Article::getCategoryId,categoryId);

        //2.状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //3.对isTop进行排序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);
        //查询categoryName(Article表中没有categoryName这个字段)，不处理会返回null
        List<Article> articles = page.getRecords();

        //解决方法：使用categoryId去查询categoryName，然后去设置
        articles.stream()
                .map(article -> {
                    //获取分类id,查询分类信息，获取分类名称
                    Category category = categoryService.getById(article.getCategoryId());
                    //把分类名称设置给article
                    article.setCategoryName(category.getName());
                    return article;
                })
                .collect(Collectors.toList());

        //用这种也可以
//        for (Article article : articles) {
//            //从分类表中查
//            Category category = categoryService.getById(article.getId());
//            article.setCategoryName(category.getName());
//        }


        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);



        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 3.4文章详情接口
     * 要求在文章列表点击阅读全文时能够跳转到文章详情页面，可以让用户阅读文章正文。
     * @param id
     * @return
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {

        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category !=null){
            articleDetailVo.setCategoryName(category.getName());
        }

        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 3.18.5 更新浏览量
     */
    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis相关的 id的浏览量
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    /**
     * 写博文
     * @param articleDto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //添加 博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);


        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(),tagId))
                .collect(Collectors.toList());

        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    /**
     * 5.11 文章列表
     */
    @Override
    public PageVo selectArticlePage(Article article, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.hasText(article.getTitle()),Article::getTitle, article.getTitle());
        queryWrapper.like(StringUtils.hasText(article.getSummary()),Article::getSummary, article.getSummary());

        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        //转换成VO
        List<Article> articles = page.getRecords();

        //这里偷懒没写VO的转换 应该转换完在设置到最后的pageVo中

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(articles);
        return pageVo;
    }

    /**
     * 5.12.3.1 查询文章详情接口
     * @param id
     * @return
     */
    @Override
    public ArticleVo getInfo(Long id) {
        Article article = getById(id);
        //获取关联标签
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        List<ArticleTag> articleTags = articleTagService.list(articleTagLambdaQueryWrapper);
        //获取标签关联id
        List<Long> tags = articleTags.stream()
                .map(articleTag -> articleTag.getTagId())
                .collect(Collectors.toList());

        ArticleVo articleVo = BeanCopyUtils.copyBean(article,ArticleVo.class);
        articleVo.setTags(tags);
        return articleVo;
    }

    /**
     * 5.12.3.2 更新文章接口
     */
    @Override
    public void edit(ArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        //更新博客信息(更新文章表)
        updateById(article);
        //删除原有的 标签和博客的关联
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(articleTagLambdaQueryWrapper);

        //添加新的博客和标签的关联信息（向标签关联表插入新的数据）
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(articleDto.getId(),tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
    }

}
