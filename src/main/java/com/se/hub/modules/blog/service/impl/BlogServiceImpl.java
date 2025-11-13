package com.se.hub.modules.blog.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.blog.exception.BlogErrorCode;
import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.entity.Blog;
import com.se.hub.modules.blog.mapper.BlogMapper;
import com.se.hub.modules.blog.repository.BlogRepository;
import com.se.hub.modules.blog.service.api.BlogService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Blog Service Implementation
 * 
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogServiceImpl implements BlogService {
    BlogRepository blogRepository;
    ProfileRepository profileRepository;
    BlogMapper blogMapper;

    @Override
    @Transactional
    public BlogResponse createBlog(CreateBlogRequest request) {
        String userId = AuthUtils.getCurrentUserId();

        Profile author = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
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
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
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
    @Transactional
    public BlogResponse updateBlogById(String blogId, UpdateBlogRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        blog = blogMapper.updateBlogFromRequest(blog, request);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        return blogMapper.toBlogResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    public void deleteBlogById(String blogId) {
        if (blogId == null || blogId.isBlank()) {
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.deleteById(blogId);
    }

    @Override
    public PagingResponse<BlogResponse> getMostPopularBlogs(PagingRequest request) {
        log.debug("BlogService_getMostPopularBlogs_Fetching most popular blogs with page: {}, size: {}", 
                request.getPage(), request.getPageSize());

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findMostPopularBlogs(pageable);

        log.debug("BlogService_getMostPopularBlogs_Found {} popular blogs", blogs.getTotalElements());

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
    public PagingResponse<BlogResponse> getMostLikedBlogs(PagingRequest request) {
        log.debug("BlogService_getMostLikedBlogs_Fetching most liked blogs with page: {}, size: {}", 
                request.getPage(), request.getPageSize());

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findMostLikedBlogs(pageable);

        log.debug("BlogService_getMostLikedBlogs_Found {} liked blogs", blogs.getTotalElements());

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
    public PagingResponse<BlogResponse> getLatestBlogs(PagingRequest request) {
        log.debug("BlogService_getLatestBlogs_Fetching latest blogs with page: {}, size: {}", 
                request.getPage(), request.getPageSize());

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findLatestBlogs(pageable);

        log.debug("BlogService_getLatestBlogs_Found {} latest blogs", blogs.getTotalElements());

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
    @Transactional
    public void incrementViewCount(String blogId) {
        if (blogId == null || blogId.isBlank()) {
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementViewCount(blogId);
        log.debug("BlogService_incrementViewCount_View count incremented for blog id {}", blogId);
    }

    @Override
    @Transactional
    public void incrementReactionCount(String blogId, int delta) {
        if (blogId == null || blogId.isBlank()) {
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementReactionCount(blogId, delta);
        log.debug("BlogService_incrementReactionCount_Reaction count incremented by {} for blog id {}", delta, blogId);
    }

    @Override
    @Transactional
    public void incrementCommentCount(String blogId, int delta) {
        if (blogId == null || blogId.isBlank()) {
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementCommentCount(blogId, delta);
        log.debug("BlogService_incrementCommentCount_Comment count incremented by {} for blog id {}", delta, blogId);
    }
}
