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
import com.se.hub.modules.blog.mapper.BlogMapper;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import com.se.hub.modules.interaction.dto.response.ReactionToggleResult;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.repository.CommentRepository;
import com.se.hub.modules.interaction.repository.ReactionRepository;
import com.se.hub.modules.interaction.service.api.ReactionService;
import com.se.hub.modules.blog.constant.BlogCacheConstants;
import com.se.hub.modules.blog.repository.BlogRepository;
import com.se.hub.modules.blog.service.BlogService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import com.se.hub.modules.profile.service.api.ActivityService;
import com.se.hub.modules.profile.service.api.ProfileProgressService;
import com.se.hub.modules.blog.service.api.BlogSettingService;
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

import java.util.List;
import java.util.Map;

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
    ActivityService activityService;
    BlogSettingService blogSettingService;
    ReactionService reactionService;
    CommentRepository commentRepository;
    ReactionRepository reactionRepository;

    /**
     * Helper method to build PagingResponse from Page<Blog>
     * Reduces code duplication across get methods
     */
    private PagingResponse<BlogResponse> buildPagingResponse(Page<Blog> blogs) {
        String currentUserId = AuthUtils.getCurrentUserIdOrNull();
        List<Blog> blogList = blogs.getContent();

        // Batch check reactions for all blogs (user-specific reaction info)
        List<String> blogIds = blogList.stream().map(Blog::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.BLOG, blogIds, currentUserId);
        
        return PagingResponse.<BlogResponse>builder()
                .currentPage(blogs.getNumber())
                .totalPages(blogs.getTotalPages())
                .pageSize(blogs.getSize())
                .totalElement(blogs.getTotalElements())
                .data(blogList.stream()
                        .map(blog -> {
                            BlogResponse response = toBlogResponseWithReaction(blog, reactionsMap.get(blog.getId()));
                            // Always read counts directly from Blog entity fields
                            response.setCmtCount(blog.getCmtCount());
                            response.setReactionCount(blog.getReactionCount());
                            return response;
                        })
                        .toList()
                )
                .build();
    }

    /**
     * Convert Blog to BlogResponse with reaction info
     */
    private BlogResponse toBlogResponseWithReaction(Blog blog, ReactionInfo reactionInfo) {
        BlogResponse response = blogMapper.toBlogResponse(blog);
        if (reactionInfo != null) {
            response.setReactions(reactionInfo);
        } else {
            response.setReactions(ReactionInfo.builder()
                    .userReacted(false)
                    .type(null)
                    .build());
        }
        // Always read counts directly from Blog entity fields
        response.setCmtCount(blog.getCmtCount());
        response.setReactionCount(blog.getReactionCount());
        return response;
    }

    /**
     * Convert Blog to BlogResponse with reaction info (single blog)
     */
    private BlogResponse toBlogResponseWithReaction(Blog blog, String userId) {
        BlogResponse response = blogMapper.toBlogResponse(blog);
        ReactionInfo reactionInfo = reactionService.getReactionsForTargets(
                TargetType.BLOG, 
                List.of(blog.getId()), 
                userId
        ).get(blog.getId());
        
        if (reactionInfo != null) {
            response.setReactions(reactionInfo);
        } else {
            response.setReactions(ReactionInfo.builder()
                    .userReacted(false)
                    .type(null)
                    .build());
        }
        // Always read counts directly from Blog entity fields
        response.setCmtCount(blog.getCmtCount());
        response.setReactionCount(blog.getReactionCount());
        return response;
    }

    /**
     * Update blog reaction count from Reaction table.
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    private void updateBlogReactionCount(Blog blog) {
        // Blocking I/O - virtual thread yields here
        long likeCount = reactionService.getReactionCount(TargetType.BLOG, blog.getId(), ReactionType.LIKE);
        // Blocking I/O - virtual thread yields here
        long dislikeCount = reactionService.getReactionCount(TargetType.BLOG, blog.getId(), ReactionType.DISLIKE);
        int totalReactionCount = (int) (likeCount - dislikeCount);
        blog.setReactionCount(totalReactionCount);
        // Blocking I/O - virtual thread yields here
        blogRepository.save(blog);
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

        // Check if approval is required
        boolean requireApproval = blogSettingService.isApprovalRequired();
        if (!requireApproval) {
            // Auto-approve if approval is not required
            blog.setIsApproved(true);
            log.debug("BlogService_createBlog_Auto-approving blog as approval mode is disabled");
        }

        Blog savedBlog = blogRepository.save(blog);
        BlogResponse response = blogMapper.toBlogResponse(savedBlog);
        
        // If blog is auto-approved, update stats and activity immediately
        if (savedBlog.getIsApproved()) {
            profileProgressService.updatePostsUploaded();
            activityService.incrementActivity(author.getId());
        } else {
            // Note: User stats and activity will be incremented only when blog is approved by admin
            // See approveBlog() method for stats and activity tracking
        }
        
        log.debug("BlogService_createBlog_Blog created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    @Transactional
    @CacheEvict(value = BlogCacheConstants.CACHE_BLOG, key = "#blogId")
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
        
        // Increment view count for each request
        incrementViewCount(blogId);
        
        // Refresh blog to get updated view count
        blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_getById_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });
        
        String currentUserId = AuthUtils.getCurrentUserId();
        BlogResponse response = toBlogResponseWithReaction(blog, currentUserId);
        return response;
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
    public PagingResponse<BlogResponse> searchBlogs(String keyword, PagingRequest request) {
        String sanitizedKeyword = keyword == null ? "" : keyword.trim();
        if (sanitizedKeyword.isBlank()) {
            log.error("BlogService_searchBlogs_Keyword is required");
            throw new AppException(ErrorCode.DATA_INVALID);
        }

        log.debug("BlogService_searchBlogs_Searching blogs with keyword: {} page: {} size: {}",
                sanitizedKeyword, request.getPage(), request.getPageSize());

        Pageable pageable = PagingUtil.createPageable(request);
        Page<Blog> blogs = blogRepository.searchApprovedBlogs(sanitizedKeyword, pageable);
        log.debug("BlogService_searchBlogs_Found {} blogs", blogs.getTotalElements());
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
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_likeBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (!blog.getIsApproved()) {
            log.error("BlogService_likeBlog_Blog {} is not approved", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        // Use ReactionService to toggle like reaction
        ReactionToggleResult toggleResult = reactionService.toggleReactionWithCount(TargetType.BLOG, blogId, ReactionType.LIKE);
        
        // Update reaction count from Reaction table
        updateBlogReactionCount(blog);
        
        // Increment activity count if reaction was added (not removed)
        String currentUserId = AuthUtils.getCurrentUserId();
        if (toggleResult.isAdded()) {
            Profile currentUser = profileRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> {
                        log.error("BlogService_likeBlog_Profile not found for user: {}", currentUserId);
                        return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                    });
            activityService.incrementActivity(currentUser.getId());
        }
        
        log.debug("BlogService_likeBlog_Reaction toggled for blog id {}", blogId);
        
        return toBlogResponseWithReaction(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_dislikeBlog_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        if (!blog.getIsApproved()) {
            log.error("BlogService_dislikeBlog_Blog {} is not approved", blogId);
            throw BlogErrorCode.BLOG_NOT_FOUND.toException();
        }

        // Use ReactionService to toggle dislike reaction
        reactionService.toggleReactionWithCount(TargetType.BLOG, blogId, ReactionType.DISLIKE);
        
        // Update reaction count from Reaction table
        updateBlogReactionCount(blog);
        
        log.debug("BlogService_dislikeBlog_Reaction toggled for blog id {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithReaction(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
        
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    log.error("BlogService_removeReaction_Blog not found with id: {}", blogId);
                    return BlogErrorCode.BLOG_NOT_FOUND.toException();
                });

        // Check if user has reacted, then toggle to remove
        boolean hasReacted = reactionService.hasUserReacted(TargetType.BLOG, blogId);
        if (hasReacted) {
            // Get current reaction type and toggle it off
            String userId = AuthUtils.getCurrentUserId();
            Map<String, ReactionInfo> reactionsMap = reactionService.getReactionsForTargets(
                    TargetType.BLOG, 
                    List.of(blogId), 
                    userId
            );
            ReactionInfo currentReaction = reactionsMap.get(blogId);
            if (currentReaction != null && currentReaction.getType() != null) {
                // Toggle the same reaction type to remove it
                reactionService.toggleReactionWithCount(TargetType.BLOG, blogId, currentReaction.getType());
            }
        }
        
        // Update reaction count from Reaction table
        updateBlogReactionCount(blog);
        
        log.debug("BlogService_removeReaction_Removed reaction from blog id {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithReaction(blogRepository.findById(blogId).orElse(blog), currentUserId);
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
        
        // Update user stats and achievements when blog is approved
        profileProgressService.updatePostsUploaded();
        
        // Increment activity count for author when blog is approved
        activityService.incrementActivity(savedBlog.getAuthor().getId());
        
        log.debug("BlogService_approveBlog_Blog approved successfully with id: {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithReaction(savedBlog, currentUserId);
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

        if (Boolean.FALSE.equals(blog.getIsApproved())) {
            log.warn("BlogService_rejectBlog_Blog {} is already rejected", blogId);
            throw BlogErrorCode.BLOG_ALREADY_REJECTED.toException();
        }

        blog.setIsApproved(false);
        blog.setUpdateBy(AuthUtils.getCurrentUserId());

        Blog savedBlog = blogRepository.save(blog);
        log.debug("BlogService_rejectBlog_Blog rejected successfully with id: {}", blogId);
        
        String currentUserId = AuthUtils.getCurrentUserId();
        return toBlogResponseWithReaction(savedBlog, currentUserId);
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
