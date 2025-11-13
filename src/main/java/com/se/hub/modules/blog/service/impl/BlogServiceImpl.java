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
import com.se.hub.modules.blog.constant.BlogCacheConstants;
import com.se.hub.modules.blog.repository.BlogRepository;
import com.se.hub.modules.blog.service.BlogService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    ProfileProgressService profileProgressService;

    /**
     * Helper method to build PagingResponse from Page<Blog>
     * Reduces code duplication across get methods
     */
    private PagingResponse<BlogResponse> buildPagingResponse(Page<Blog> blogs) {
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
    @CacheEvict(value = {
            BlogCacheConstants.CACHE_BLOGS,
            BlogCacheConstants.CACHE_POPULAR_BLOGS,
            BlogCacheConstants.CACHE_LIKED_BLOGS,
            BlogCacheConstants.CACHE_LATEST_BLOGS
    }, allEntries = true)
    public BlogResponse createBlog(CreateBlogRequest request) {
        log.debug("BlogService_createBlog_Creating new blog for user: {}", AuthUtils.getCurrentUserId());
        String userId = AuthUtils.getCurrentUserId();

        Profile author = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("BlogService_createBlog_Profile not found for user: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        Blog blog = blogMapper.toBlog(request);
        blog.setAuthor(author);
        blog.setCreatedBy(userId);
        blog.setUpdateBy(userId);

        BlogResponse response = blogMapper.toBlogResponse(blogRepository.save(blog));
        
        // Update user stats after blog creation
        profileProgressService.updatePostsUploaded();
        
        log.debug("BlogService_createBlog_Blog created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_BLOG, key = "#blogId")
    public BlogResponse getById(String blogId) {
        log.debug("BlogService_getById_Fetching blog with id: {}", blogId);
        return blogMapper.toBlogResponse(blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_getById_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                }));
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_BLOGS_BY_AUTHOR, 
            key = "#authorId + '_' + #request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
    public PagingResponse<BlogResponse> getBlogsByAuthorId(String authorId, PagingRequest request) {
        log.debug("BlogService_getBlogsByAuthorId_Fetching blogs for author: {} with page: {}, size: {}", 
                authorId, request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findAllByAuthor_Id(authorId, pageable);
        return buildPagingResponse(blogs);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_BLOGS, 
            key = "#request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
    public PagingResponse<BlogResponse> getBlogs(PagingRequest request) {
        log.debug("BlogService_getBlogs_Fetching blogs with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Blog> blogs = blogRepository.findAll(pageable);
        return buildPagingResponse(blogs);
    }

    @Override
    @Transactional
    @CacheEvict(value = {
            BlogCacheConstants.CACHE_BLOG,
            BlogCacheConstants.CACHE_BLOGS,
            BlogCacheConstants.CACHE_BLOGS_BY_AUTHOR,
            BlogCacheConstants.CACHE_POPULAR_BLOGS,
            BlogCacheConstants.CACHE_LIKED_BLOGS,
            BlogCacheConstants.CACHE_LATEST_BLOGS
    }, allEntries = true)
    public BlogResponse updateBlogById(String blogId, UpdateBlogRequest request) {
        log.debug("BlogService_updateBlogById_Updating blog with id: {}", blogId);
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_updateBlogById_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        blog = blogMapper.updateBlogFromRequest(blog, request);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        BlogResponse response = blogMapper.toBlogResponse(blogRepository.save(blog));
        log.debug("BlogService_updateBlogById_Blog updated successfully with id: {}", blogId);
        return response;
    }

    @Override
    @Transactional
    @CacheEvict(value = {
            BlogCacheConstants.CACHE_BLOG,
            BlogCacheConstants.CACHE_BLOGS,
            BlogCacheConstants.CACHE_BLOGS_BY_AUTHOR,
            BlogCacheConstants.CACHE_POPULAR_BLOGS,
            BlogCacheConstants.CACHE_LIKED_BLOGS,
            BlogCacheConstants.CACHE_LATEST_BLOGS
    }, allEntries = true)
    public void deleteBlogById(String blogId) {
        log.debug("BlogService_deleteBlogById_Deleting blog with id: {}", blogId);
        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_deleteBlogById_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            log.error("BlogService_deleteBlogById_Blog not found with id: {}", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.deleteById(blogId);
        log.debug("BlogService_deleteBlogById_Blog deleted successfully with id: {}", blogId);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_POPULAR_BLOGS, 
            key = "#request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
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
        return buildPagingResponse(blogs);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_LIKED_BLOGS, 
            key = "#request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
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
        return buildPagingResponse(blogs);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_LATEST_BLOGS, 
            key = "#request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
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
        return buildPagingResponse(blogs);
    }

    @Override
    @Transactional
    public void incrementViewCount(String blogId) {
        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_incrementViewCount_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            log.error("BlogService_incrementViewCount_Blog not found with id: {}", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementViewCount(blogId);
        log.debug("BlogService_incrementViewCount_View count incremented for blog id {}", blogId);
    }

    @Override
    @Transactional
    public void incrementReactionCount(String blogId, int delta) {
        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_incrementReactionCount_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            log.error("BlogService_incrementReactionCount_Blog not found with id: {}", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementReactionCount(blogId, delta);
        log.debug("BlogService_incrementReactionCount_Reaction count incremented by {} for blog id {}", delta, blogId);
    }

    @Override
    @Transactional
    public void incrementCommentCount(String blogId, int delta) {
        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_incrementCommentCount_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        if (!blogRepository.existsById(blogId)) {
            log.error("BlogService_incrementCommentCount_Blog not found with id: {}", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        blogRepository.incrementCommentCount(blogId, delta);
        log.debug("BlogService_incrementCommentCount_Comment count incremented by {} for blog id {}", delta, blogId);
    }
}
