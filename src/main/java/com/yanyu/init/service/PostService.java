package com.yanyu.init.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyu.init.model.dto.post.PostQueryRequest;
import com.yanyu.init.model.entity.Post;
import com.yanyu.init.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子服务
 */
public interface PostService extends IService<Post> {

    /**
     * 文章校验
     *
     * @param post post对象
     * @param add  是否参数验证
     */
    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 分页获取文章封装(需要登录)
     *
     * @param postPage 文章分页
     * @param request  客户端请求
     * @return 分页获取文章封装
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    /**
     * 分页获取文章
     *
     * @param postQueryRequest 查询请求
     * @param request          客户端请求
     * @return 分页获取文章封装
     */
    Page<PostVO> listPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 分页获取文章封装(无需登录)
     *
     * @param postPage 文章分页
     * @param request  客户端请求
     * @return 分页获取文章封装
     */
    Page<PostVO> getPostVOPagePermitNull(Page<Post> postPage, HttpServletRequest request);

}
