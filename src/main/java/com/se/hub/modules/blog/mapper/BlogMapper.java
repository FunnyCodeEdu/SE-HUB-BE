package com.se.hub.modules.blog.mapper;

import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    Blog toBlog(CreateBlogRequest request);
    BlogResponse toBlogResponse(Blog blog);
    Blog updateBlogFromRequest(@MappingTarget Blog blog, UpdateBlogRequest request);
}
