package com.catsocute.japanlearn_hub.modules.blog.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.modules.blog.dto.request.CreateBlogRequest;
import com.catsocute.japanlearn_hub.modules.blog.dto.request.UpdateBlogRequest;
import com.catsocute.japanlearn_hub.modules.blog.dto.response.BlogResponse;
import com.catsocute.japanlearn_hub.modules.blog.entity.Blog;
import com.catsocute.japanlearn_hub.modules.blog.mapper.BlogMapper;
import com.catsocute.japanlearn_hub.modules.blog.repository.BlogRepository;
import com.catsocute.japanlearn_hub.modules.blog.service.api.BlogService;
import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import com.catsocute.japanlearn_hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogServiceImpl implements BlogService {
    BlogRepository blogRepository;
    ProfileRepository profileRepository;
    BlogMapper blogMapper;

    @Override
    public BlogResponse createBlog(CreateBlogRequest request) {
        String userId = AuthUtils.getCurrentUserId();

        Profile author = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Profile not found for user id {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        Blog blog = blogMapper.toBlog(request);
        blog.setAuthor(author);
        blog.setCreatedBy(userId);
        blog.setUpdateBy(userId);

        return blogMapper.toBlogResponse(blogRepository.save(blog));
    }

    @Override
    public BlogResponse getById(String blogId) {
        return blogMapper.toBlogResponse(blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_getById_Blog id {} not found", blogId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<BlogResponse> getBlogsByAuthorId(String authorId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findAllByAuthor_Id(authorId, pageable);

        return PagingResponse.<BlogResponse>builder()
                .currentPage(blogs.getNumber())
                .totalPages(blogs.getTotalPages())
                .pageSize(blogs.getSize())
                .totalElement(blogs.getTotalElements())
                .data(blogs.getContent().stream()
                        .map(blogMapper::toBlogResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<BlogResponse> getBlogs(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findAll(pageable);

        return PagingResponse.<BlogResponse>builder()
                .currentPage(blogs.getNumber())
                .totalPages(blogs.getTotalPages())
                .pageSize(blogs.getSize())
                .totalElement(blogs.getTotalElements())
                .data(blogs.getContent().stream()
                        .map(blogMapper::toBlogResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public BlogResponse updateBlogById(String blogId, UpdateBlogRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_updateBlogById_Blog id {} not found", blogId);
                    return new AppException(ErrorCode.DATA_NOT_FOUND);
                });

        blog = blogMapper.updateBlogFromRequest(blog, request);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        return blogMapper.toBlogResponse(blogRepository.save(blog));
    }

    @Override
    public void deleteBlogById(String blogId) {
        blogRepository.deleteById(blogId);
    }
}
