package com.se.hub.modules.blog.service.impl;

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
import com.se.hub.modules.blog.entity.BlogReaction;
import com.se.hub.modules.blog.mapper.BlogMapper;
import com.se.hub.modules.blog.constant.BlogCacheConstants;
import com.se.hub.modules.blog.repository.BlogRepository;
import com.se.hub.modules.blog.repository.BlogReactionRepository;
import com.se.hub.modules.blog.service.BlogService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
    BlogReactionRepository blogReactionRepository;
    ProfileRepository profileRepository;
    BlogMapper blogMapper;
    ProfileProgressService profileProgressService;

    /**
     * Helper method to build PagingResponse from Page<Blog>
     * Reduces code duplication across get methods
     */
    private PagingResponse<BlogResponse> buildPagingResponse(Page<Blog> blogs) {
        String currentUserId = AuthUtils.getCurrentUserId();
        return PagingResponse.<BlogResponse>builder()
                .currentPage(blogs.getNumber())
                .totalPages(blogs.getTotalPages())
                .pageSize(blogs.getSize())
                .totalElement(blogs.getTotalElements())
                .data(blogs.getContent().stream()
                        .map(blog -> toBlogResponseWithIsLiked(blog, currentUserId))
                        .toList()
                )
                .build();
    }

    /**
     * Convert Blog to BlogResponse with isLiked field
     */
    private BlogResponse toBlogResponseWithIsLiked(Blog blog, String userId) {
        BlogResponse response = blogMapper.toBlogResponse(blog);
        if (userId != null && !userId.isBlank()) {
            Profile profile = profileRepository.findByUserId(userId).orElse(null);
            if (profile != null) {
                blogReactionRepository.findByBlog_IdAndUser_Id(blog.getId(), profile.getId())
                        .ifPresentOrElse(
                                reaction -> response.setIsLiked(reaction.getIsLike()),
                                () -> response.setIsLiked(null)
                        );
            }
        }
        return response;
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
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_getById_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });
        
        // Only return approved blogs (unless admin)
        if (!blog.getIsApproved() && !isAdmin()) {
            log.error("BlogService_getById_Blog {} is not approved", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(blog, currentUserId);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_BLOGS_BY_AUTHOR, 
            key = "#authorId + '_' + #request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
    public PagingResponse<BlogResponse> getBlogsByAuthorId(String authorId, PagingRequest request) {
        log.debug("BlogService_getBlogsByAuthorId_Fetching blogs for author: {} with page: {}, size: {}", 
                authorId, request.getPage(), request.getPageSize());
        Pageable pageable = PagingUtil.createPageable(request);

        // Only show approved blogs (unless admin viewing their own blogs)
        Page<Blog> blogs;
        if (isAdmin()) {
            blogs = blogRepository.findAllByAuthor_Id(authorId, pageable);
        } else {
            blogs = blogRepository.findAllApprovedByAuthor_Id(authorId, pageable);
        }
        return buildPagingResponse(blogs);
    }

    @Override
    @Cacheable(value = BlogCacheConstants.CACHE_BLOGS, 
            key = "#request.page + '_' + #request.pageSize + '_' + (#request.sortRequest?.direction ?: 'desc') + '_' + (#request.sortRequest?.field ?: 'createDate')")
    public PagingResponse<BlogResponse> getBlogs(PagingRequest request) {
        log.debug("BlogService_getBlogs_Fetching blogs with page: {}, size: {}", 
                request.getPage(), request.getPageSize());
        Pageable pageable = PagingUtil.createPageable(request);

        // Only show approved blogs (unless admin)
        Page<Blog> blogs;
        if (isAdmin()) {
            blogs = blogRepository.findAll(pageable);
        } else {
            blogs = blogRepository.findAllApproved(pageable);
        }
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

        Pageable pageable = PagingUtil.createPageable(request);

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

        Pageable pageable = PagingUtil.createPageable(request);

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

        Pageable pageable = PagingUtil.createPageable(request);

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
    public BlogResponse likeBlog(String blogId) {
        log.debug("BlogService_likeBlog_Liking blog with id: {}", blogId);
        String userId = AuthUtils.getCurrentUserId();
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_likeBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (!blog.getIsApproved()) {
            log.error("BlogService_likeBlog_Blog {} is not approved", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("BlogService_likeBlog_Profile not found for user: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        BlogReaction existingReaction = blogReactionRepository
                .findByBlog_IdAndUser_Id(blogId, profile.getId())
                .orElse(null);

        if (existingReaction != null) {
            if (Boolean.TRUE.equals(existingReaction.getIsLike())) {
                log.debug("BlogService_likeBlog_User already liked this blog");
                // Already liked, return current state
            } else {
                // Change from dislike to like
                existingReaction.setIsLike(true);
                blogReactionRepository.save(existingReaction);
                blogRepository.incrementReactionCount(blogId, 2); // +1 for like, +1 to cancel dislike
                log.debug("BlogService_likeBlog_Changed dislike to like for blog id {}", blogId);
            }
        } else {
            // Create new like reaction
            BlogReaction reaction = BlogReaction.builder()
                    .blog(blog)
                    .user(profile)
                    .isLike(true)
                    .build();
            reaction.setCreatedBy(userId);
            reaction.setUpdateBy(userId);
            blogReactionRepository.save(reaction);
            blogRepository.incrementReactionCount(blogId, 1);
            log.debug("BlogService_likeBlog_Created new like reaction for blog id {}", blogId);
        }

        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
    public BlogResponse dislikeBlog(String blogId) {
        log.debug("BlogService_dislikeBlog_Disliking blog with id: {}", blogId);
        String userId = AuthUtils.getCurrentUserId();
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_dislikeBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (!blog.getIsApproved()) {
            log.error("BlogService_dislikeBlog_Blog {} is not approved", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("BlogService_dislikeBlog_Profile not found for user: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        BlogReaction existingReaction = blogReactionRepository
                .findByBlog_IdAndUser_Id(blogId, profile.getId())
                .orElse(null);

        if (existingReaction != null) {
            if (Boolean.FALSE.equals(existingReaction.getIsLike())) {
                log.debug("BlogService_dislikeBlog_User already disliked this blog");
                // Already disliked, return current state
            } else {
                // Change from like to dislike
                existingReaction.setIsLike(false);
                blogReactionRepository.save(existingReaction);
                blogRepository.incrementReactionCount(blogId, -2); // -1 for like, -1 for dislike
                log.debug("BlogService_dislikeBlog_Changed like to dislike for blog id {}", blogId);
            }
        } else {
            // Create new dislike reaction
            BlogReaction reaction = BlogReaction.builder()
                    .blog(blog)
                    .user(profile)
                    .isLike(false)
                    .build();
            reaction.setCreatedBy(userId);
            reaction.setUpdateBy(userId);
            blogReactionRepository.save(reaction);
            blogRepository.incrementReactionCount(blogId, -1);
            log.debug("BlogService_dislikeBlog_Created new dislike reaction for blog id {}", blogId);
        }

        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
    public BlogResponse removeReaction(String blogId) {
        log.debug("BlogService_removeReaction_Removing reaction from blog with id: {}", blogId);
        String userId = AuthUtils.getCurrentUserId();
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_removeReaction_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("BlogService_removeReaction_Profile not found for user: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        BlogReaction existingReaction = blogReactionRepository
                .findByBlog_IdAndUser_Id(blogId, profile.getId())
                .orElse(null);

        if (existingReaction != null) {
            int delta = Boolean.TRUE.equals(existingReaction.getIsLike()) ? -1 : 1;
            blogReactionRepository.delete(existingReaction);
            blogRepository.incrementReactionCount(blogId, delta);
            log.debug("BlogService_removeReaction_Removed reaction from blog id {}", blogId);
        }

        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
    public BlogResponse approveBlog(String blogId) {
        log.debug("BlogService_approveBlog_Approving blog with id: {}", blogId);
        
        checkAdminPermission();

        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_approveBlog_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_approveBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (Boolean.TRUE.equals(blog.getIsApproved())) {
            log.warn("BlogService_approveBlog_Blog {} is already approved", blogId);
            throw BlogErrorCode.BLOG_ALREADY_APPROVED.toException();
        }

        blog.setIsApproved(true);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        Blog savedBlog = blogRepository.save(blog);
        log.debug("BlogService_approveBlog_Blog approved successfully with id: {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(savedBlog, currentUserId);
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
    public BlogResponse rejectBlog(String blogId) {
        log.debug("BlogService_rejectBlog_Rejecting blog with id: {}", blogId);
        
        checkAdminPermission();

        if (blogId == null || blogId.isBlank()) {
            log.error("BlogService_rejectBlog_Blog ID is required");
            throw BlogErrorCode.BLOG_ID_REQUIRED.toException();
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_rejectBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (Boolean.FALSE.equals(blog.getIsApproved()) && blog.getIsApproved() != null) {
            log.warn("BlogService_rejectBlog_Blog {} is already rejected", blogId);
            throw BlogErrorCode.BLOG_ALREADY_REJECTED.toException();
        }

        blog.setIsApproved(false);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        Blog savedBlog = blogRepository.save(blog);
        log.debug("BlogService_rejectBlog_Blog rejected successfully with id: {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithIsLiked(savedBlog, currentUserId);
    }

    @Override
    public PagingResponse<BlogResponse> getPendingBlogs(PagingRequest request) {
        log.debug("BlogService_getPendingBlogs_Fetching pending blogs with page: {}, size: {}",
                request.getPage(), request.getPageSize());

        checkAdminPermission();

        Pageable pageable = PagingUtil.createPageable(request);
        Page<Blog> blogs = blogRepository.findAllByIsApprovedFalse(pageable);
        
        log.debug("BlogService_getPendingBlogs_Found {} pending blogs", blogs.getTotalElements());
        return buildPagingResponse(blogs);
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    /**
     * Check admin permission and throw exception if not authorized
     */
    private void checkAdminPermission() {
        if (!isAdmin()) {
            log.error("BlogService_checkAdminPermission_User {} is not admin", AuthUtils.getCurrentUserId());
            throw BlogErrorCode.BLOG_FORBIDDEN_OPERATION.toException();
        }
    }
}
