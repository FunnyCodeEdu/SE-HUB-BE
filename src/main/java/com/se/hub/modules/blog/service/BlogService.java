package com.se.hub.modules.blog.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;

public interface BlogService {
    /**
     * create new blog
     * @author catsocute
     */
    BlogResponse createBlog(CreateBlogRequest request);

    /**
     * get blog by id
     * @author catsocute
     */
    BlogResponse getById(String blogId);

    /**
     * get blogs by author id
     * @author catsocute
     */
    PagingResponse<BlogResponse> getBlogsByAuthorId(String authorId, PagingRequest request);

    /**
     * get blogs
     * @author catsocute
     */
    PagingResponse<BlogResponse> getBlogs(PagingRequest request);

    /**
     * update blog by id
     * @author catsocute
     */
    BlogResponse updateBlogById(String blogId, UpdateBlogRequest request);

    /**
     * delete blog by id
     * @author catsocute
     */
    void deleteBlogById(String blogId);

    /**
     * Get most popular blogs sorted by view count
     * @param request paging request
     * @return paging response with popular blogs
     */
    PagingResponse<BlogResponse> getMostPopularBlogs(PagingRequest request);

    /**
     * Get most liked blogs sorted by reaction count
     * @param request paging request
     * @return paging response with most liked blogs
     */
    PagingResponse<BlogResponse> getMostLikedBlogs(PagingRequest request);

    /**
     * Get latest blogs sorted by created date
     * @param request paging request
     * @return paging response with latest blogs
     */
    PagingResponse<BlogResponse> getLatestBlogs(PagingRequest request);

    /**
     * Search approved blogs by keyword (content or author name)
     * @param keyword keyword to search
     * @param request paging request
     * @return paging response with matched blogs
     */
    PagingResponse<BlogResponse> searchBlogs(String keyword, PagingRequest request);

    /**
     * Increment view count for a blog (atomic operation)
     * @param blogId blog ID
     */
    void incrementViewCount(String blogId);

    /**
     * Increment reaction count for a blog (atomic operation)
     * @param blogId blog ID
     * @param delta positive for like, negative for unlike
     */
    void incrementReactionCount(String blogId, int delta);

    /**
     * Increment comment count for a blog (atomic operation)
     * @param blogId blog ID
     * @param delta positive for add comment, negative for delete comment
     */
    void incrementCommentCount(String blogId, int delta);

    /**
     * Like a blog
     * @param blogId blog ID
     * @return BlogResponse with updated reaction count
     */
    BlogResponse likeBlog(String blogId);

    /**
     * Dislike a blog
     * @param blogId blog ID
     * @return BlogResponse with updated reaction count
     */
    BlogResponse dislikeBlog(String blogId);

    /**
     * Remove reaction from a blog
     * @param blogId blog ID
     * @return BlogResponse with updated reaction count
     */
    BlogResponse removeReaction(String blogId);

    /**
     * Approve a blog (admin only)
     * @param blogId blog ID
     * @return BlogResponse
     */
    BlogResponse approveBlog(String blogId);

    /**
     * Reject a blog (admin only)
     * @param blogId blog ID
     * @return BlogResponse
     */
    BlogResponse rejectBlog(String blogId);

    /**
     * Get pending blogs (admin only)
     * @param request paging request
     * @return paging response with pending blogs
     */
    PagingResponse<BlogResponse> getPendingBlogs(PagingRequest request);
}
