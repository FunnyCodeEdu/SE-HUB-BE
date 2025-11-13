package com.se.hub.modules.blog.service.api;

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
}
