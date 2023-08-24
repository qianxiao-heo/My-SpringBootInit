package com.yanyu.init.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yanyu.init.annotation.AuthCheck;
import com.yanyu.init.common.BaseResponse;
import com.yanyu.init.common.ErrorCode;
import com.yanyu.init.common.ResultUtils;
import com.yanyu.init.constant.UserConstant;
import com.yanyu.init.exception.BusinessException;
import com.yanyu.init.model.dto.post.PostAddRequest;
import com.yanyu.init.model.dto.post.PostDeleteRequest;
import com.yanyu.init.model.dto.post.PostQueryRequest;
import com.yanyu.init.model.dto.post.PostUpdateRequest;
import com.yanyu.init.model.entity.Post;
import com.yanyu.init.model.entity.User;
import com.yanyu.init.model.vo.PostVO;
import com.yanyu.init.service.PostService;
import com.yanyu.init.service.UserService;
import com.yanyu.init.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    /**
     * @param postAddRequest 新增文章请求体
     * @param request 客户端请求
     * @return 新增文章id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post=new Post();
        BeanUtils.copyProperties(postAddRequest,post);
        List<String> tags=postAddRequest.getTags();
        if (tags != null){
            post.setTags(GSON.toJson(tags));
        }
        //文章参数检验
        postService.validPost(post,true);
        // 获取已登录用户
        User loginUser=userService.getLoginUser(request);
        // 将已登录用户id设置到post表的用户id中
        post.setUserId(loginUser.getId());
        // 将文章的收藏数和点赞数初始为0
        post.setFavourNum(0);
        post.setThumbNum(0);
        // 执行文章保存并获取结果
        boolean result = postService.save(post);
        // 验证执行结果
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        long newPost=post.getId();
        return ResultUtils.success(newPost);
    }

    /**
     * @param postDeleteRequest 删除文章请求体
     * @param request 客户端请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePost(@RequestBody PostDeleteRequest postDeleteRequest,HttpServletRequest request){
        if (postDeleteRequest==null || postDeleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id=postDeleteRequest.getId();
        // 判断文章是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * @param postUpdateRequest 修改文章请求体
     * @return 修改结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest){
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post upPost=new Post();
        BeanUtils.copyProperties(postUpdateRequest,upPost);
        List<String> tags=postUpdateRequest.getTags();
        if (tags!=null){
            upPost.setTags(GSON.toJson(tags));
        }
        //文章参数检验
        postService.validPost(upPost,true);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.updateById(upPost);
        return ResultUtils.success(result);
    }
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,HttpServletRequest request){
        if (postQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long pageSize = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20,ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(postQueryRequest.getCurrent(), postQueryRequest.getPageSize()),
                postService.getQueryWrapper(postQueryRequest));
        Page<PostVO> postVOPage=postService.getPostVOPagePermitNull(postPage, request);
        return ResultUtils.success(postVOPage);
    }
}
