package com.sangeng.utils;

import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.HotArticleVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 3.2 Bean拷贝工具类
 */
public class BeanCopyUtils {
    public BeanCopyUtils(){}

    /**
     * Bean拷贝工具类
     * @param source 源数据对象
     * @param clazz  新对象字节码，用来创建新对象
     * @param <V>
     * @return
     */
    public static <V> V copyBean(Object source,Class<V> clazz) {

        V result = null;
        try {
            //创建目标对象
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回结果
        return result;
    }

    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }


    //测试
    public static void main(String[] args) {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("ss");

        HotArticleVo hotArticleVo = copyBean(article, HotArticleVo.class);
        System.out.println(hotArticleVo);
    }
}
