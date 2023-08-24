package com.yanyu.init.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyu.init.common.ErrorCode;
import com.yanyu.init.constant.CommonConstant;
import com.yanyu.init.exception.BusinessException;
import com.yanyu.init.mapper.PostFavourMapper;
import com.yanyu.init.mapper.PostMapper;
import com.yanyu.init.mapper.PostThumbMapper;
import com.yanyu.init.model.dto.post.PostQueryRequest;
import com.yanyu.init.model.entity.Post;
import com.yanyu.init.model.entity.PostFavour;
import com.yanyu.init.model.entity.PostThumb;
import com.yanyu.init.model.entity.User;
import com.yanyu.init.model.vo.PostVO;
import com.yanyu.init.model.vo.UserVO;
import com.yanyu.init.service.PostService;
import com.yanyu.init.service.UserService;
import com.yanyu.init.utils.SqlUtils;
import com.yanyu.init.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserService userService;
    @Resource
    private PostMapper postMapper;
    @Resource
    private PostThumbMapper postThumbMapper;
    @Resource
    private PostFavourMapper postFavourMapper;

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 文章参数校验
        if (StringUtils.isNotBlank(title) && title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题长度过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容过长");
        }
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();//搜索词
        String sortField = postQueryRequest.getSortField();//排序字段
        String sortOrder = postQueryRequest.getSortOrder();//排序顺序（默认升序）
        Long id = postQueryRequest.getId();//文章id
        String title = postQueryRequest.getTitle();//文章标题
        String content = postQueryRequest.getContent();//文章内容
        List<String> tagList = postQueryRequest.getTags();//文章标签
        Long userId = postQueryRequest.getUserId();//文章所属用户id
        Long notId = postQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }
        // 关联查询用户信息
        Set<Long> userId = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        // 查询（根据ID 批量查询）
        Map<Long, List<User>> userListMap = userService.listByIds(userId).stream().collect(Collectors.groupingBy(User::getId));
        // 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            Set<Long> postId = postList.stream().map(Post::getId).collect(Collectors.toSet());//获取文章id
            loginUser = userService.getLoginUser(request);
            // 获取文章点赞
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("postId", postId);
            postThumbQueryWrapper.eq("userId", loginUser.getId());
            List<PostThumb> postThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
            postThumbList.forEach(postThumb -> postIdHasThumbMap.put(postThumb.getPostId(), true));
            // 获取文章收藏
            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
            postFavourQueryWrapper.in("postId", postId);
            postFavourQueryWrapper.eq("userId", loginUser.getId());
            List<PostFavour> postFavourList = postFavourMapper.selectList(postFavourQueryWrapper);
            postFavourList.forEach(postFavour -> postIdHasFavourMap.put(postFavour.getPostId(), true));
        }
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post);
            setPostVOUser(postVO, post, userListMap);
            // 填入文章点赞数
            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
            // 填入文章收藏数
            postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    @Override
    public Page<PostVO> listPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<Post> postPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(postQueryRequest));
        return this.getPostVOPage(postPage, request);
    }

    @Override
    public Page<PostVO> getPostVOPagePermitNull(Page<Post> postPage, HttpServletRequest request) {
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        List<Post> pageRecords = postPage.getRecords();
        Set<Long> userId = pageRecords.stream().map(Post::getUserId).collect(Collectors.toSet());
        // 查询（根据ID 批量查询）
        Map<Long, List<User>> userListMap = userService.listByIds(userId).stream().collect(Collectors.groupingBy(User::getId));
        List<PostVO> postVOList = pageRecords.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post);
            return setPostVOUser(postVO, post, userListMap);
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    /**
     * 抽象公共方法
     *
     * @param postVO      文章视图
     * @param post        文章类
     * @param userListMap 对象集合
     * @return postVO
     */
    private PostVO setPostVOUser(PostVO postVO, Post post, Map<Long, List<User>> userListMap) {
        Long postUserId = post.getUserId();
        User user = null;
        if (userListMap.containsKey(postUserId)) {
            user = userListMap.get(postUserId).get(0);
        }
        // 填入脱敏后的用户信息
        postVO.setUser(userService.getSafetyUser(user));
        return postVO;
    }
}
