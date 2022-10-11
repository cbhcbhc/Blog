package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.service.LinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Resource
    private LinkService linkService;

    /**
     * 3.5 友联查询
     * @return
     */
    @GetMapping("/getAllLink")
    public ResponseResult getAllLink(){
        return linkService.getAllLink();
    }
}
