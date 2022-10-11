package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.ArticleVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/content/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 5.8.3.4 新增博文接口
     * @param article
     * @return
     */
    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }


    /**
     * 5.11 文章列表
     */
    @GetMapping("/list")
    public ResponseResult list(Article article, Integer pageNum, Integer pageSize)
    {
        PageVo pageVo = articleService.selectArticlePage(article,pageNum,pageSize);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 5.12.3.1 查询文章详情接口
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        ArticleVo article = articleService.getInfo(id);
        return ResponseResult.okResult(article);
    }

    /**
     * 5.12.3.2 更新文章接口
     */
    @PutMapping
    public ResponseResult edit(@RequestBody ArticleDto article){
        articleService.edit(article);
        return ResponseResult.okResult();
    }

    /**
     * 5.13 删除文章
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        articleService.removeById(id);
        return ResponseResult.okResult();
    }

}
