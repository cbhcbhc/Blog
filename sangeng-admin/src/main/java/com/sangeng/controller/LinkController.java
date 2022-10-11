package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Link;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.LinkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/content/link")
public class LinkController {
    @Resource
    private LinkService linkService;

    /**
     * 5.31 分页查询获取友链列表
     */
    @GetMapping("/list")
    public ResponseResult list(Link link, Integer pageNum, Integer pageSize)
    {
        PageVo pageVo = linkService.selectLinkPage(link,pageNum,pageSize);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 5.32 新增友链
     * @param link
     * @return
     */
    @PostMapping
    public ResponseResult add(@RequestBody Link link){
        linkService.save(link);
        return ResponseResult.okResult();
    }

    /**
     * 5.33 修改友链
     */
    /**
     * 5.33.2.1 根据id查询友联(数据回显)
     */
    @GetMapping(value = "/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Link link = linkService.getById(id);
        return ResponseResult.okResult(link);
    }

    /**
     * 5.33.2.2 修改（更新）友链
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    /**
     * 5.34 删除友链
     */
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 修改友链的状态
     */
    @PutMapping("/changeLinkStatus")
    public ResponseResult changeLinkStatus(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }
}
