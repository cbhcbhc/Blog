package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 3.1 热门文章
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//响应格式优化，毕竟实体类中多了很多字段
public class HotArticleVo {
    private Long id;
    //标题
    private String title;
    //访问量
    private Long viewCount;
}
