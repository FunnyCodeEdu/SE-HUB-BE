package com.se.hub.modules.blog.mapper;

import com.se.hub.modules.blog.dto.request.CreateBlogRequest;
import com.se.hub.modules.blog.dto.request.UpdateBlogRequest;
import com.se.hub.modules.blog.dto.response.BlogAuthorResponse;
import com.se.hub.modules.blog.dto.response.BlogResponse;
import com.se.hub.modules.blog.entity.Blog;
import com.se.hub.modules.profile.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BlogMapper {
    Blog toBlog(CreateBlogRequest request);
    BlogResponse toBlogResponse(Blog blog);
    Blog updateBlogFromRequest(@MappingTarget Blog blog, UpdateBlogRequest request);
    BlogAuthorResponse toBlogAuthorResponse(Profile profile);
    
    /**
     * Map list of Blog entities to list of BlogResponse DTOs
     * @param blogs list of Blog entities
     * @return list of BlogResponse DTOs
     */
    List<BlogResponse> toListBlogResponse(List<Blog> blogs);
}
