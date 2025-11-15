package com.se.hub.modules.interaction.mapper;

import com.se.hub.modules.interaction.dto.request.CreateCommentRequest;
import com.se.hub.modules.interaction.dto.request.UpdateCommentRequest;
import com.se.hub.modules.interaction.dto.response.CommentResponse;
import com.se.hub.modules.interaction.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "mentions", source = "mentions")
    Comment toComment(CreateCommentRequest request);
    
    @Mapping(target = "authorId", source = "author.user.id")
    @Mapping(target = "authorName", source = "author.fullName")
    @Mapping(target = "authorAvatar", source = "author.avtUrl")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", source = "replies")
    CommentResponse toCommentResponse(Comment comment);
    
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "targetType", ignore = true)
    @Mapping(target = "targetId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    Comment updateCommentFromRequest(@MappingTarget Comment comment, UpdateCommentRequest request);
}

