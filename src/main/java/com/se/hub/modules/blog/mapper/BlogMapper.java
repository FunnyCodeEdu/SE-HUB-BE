package com.se.hub.modules.blog.mapper;

import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BlogMapper {
    Blog toBlog(CreateBlogRequest request);
    BlogResponse toBlogResponse(Blog blog);
    Blog updateBlogFromRequest(@MappingTarget Blog blog, UpdateBlogRequest request);
    
    /**
     * Map list of Blog entities to list of BlogResponse DTOs
     * @param blogs list of Blog entities
     * @return list of BlogResponse DTOs
     */
    List<BlogResponse> toListBlogResponse(List<Blog> blogs);
}
