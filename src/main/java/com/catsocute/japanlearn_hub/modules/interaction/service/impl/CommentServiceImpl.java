package com.catsocute.japanlearn_hub.modules.interaction.service.impl;

import com.catsocute.japanlearn_hub.common.constant.GlobalVariable;
import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.catsocute.japanlearn_hub.common.exception.AppException;
import com.catsocute.japanlearn_hub.common.utils.PagingUtil;
import com.catsocute.japanlearn_hub.modules.auth.utils.AuthUtils;
import com.catsocute.japanlearn_hub.modules.interaction.dto.request.CreateCommentRequest;
import com.catsocute.japanlearn_hub.modules.interaction.dto.request.UpdateCommentRequest;
import com.catsocute.japanlearn_hub.modules.interaction.dto.response.CommentResponse;
import com.catsocute.japanlearn_hub.modules.interaction.entity.Comment;
import com.catsocute.japanlearn_hub.modules.interaction.enums.TargetType;
import com.catsocute.japanlearn_hub.modules.interaction.mapper.CommentMapper;
import com.catsocute.japanlearn_hub.modules.interaction.repository.CommentRepository;
import com.catsocute.japanlearn_hub.modules.interaction.service.api.CommentService;
import com.catsocute.japanlearn_hub.modules.profile.entity.Profile;
import com.catsocute.japanlearn_hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ProfileRepository profileRepository;

    @Override
    public CommentResponse createComment(CreateCommentRequest request) {
        Comment comment = commentMapper.toComment(request);
        
        // Get current user and set as author
        String userId = AuthUtils.getCurrentUserId();
        Profile author = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_createComment_Profile for user id {} not found", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });
        comment.setAuthor(author);
        
        // Set parent comment if provided
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> {
                        log.error("CommentServiceImpl_createComment_Parent comment id {} not found", request.getParentCommentId());
                        return new AppException(ErrorCode.COMMENT_PARENT_INVALID);
                    });
            comment.setParentComment(parentComment);
        }
        
        comment.setCreatedBy(userId);
        comment.setUpdateBy(userId);

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse getById(String commentId) {
        return commentMapper.toCommentResponse(commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_getById_Comment id {} not found", commentId);
                    return new AppException(ErrorCode.COMMENT_NOT_FOUND);
                }));
    }

    @Override
    public PagingResponse<CommentResponse> getCommentsByTarget(String targetType, String targetId, PagingRequest request) {
        TargetType type;
        try {
            type = TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("CommentServiceImpl_getCommentsByTarget_Invalid target type: {}", targetType);
            throw new AppException(ErrorCode.COMMENT_TARGET_TYPE_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Comment> comments = commentRepository.findByTargetTypeAndTargetId(type, targetId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<CommentResponse> getParentCommentsByTarget(String targetType, String targetId, PagingRequest request) {
        TargetType type;
        try {
            type = TargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("CommentServiceImpl_getParentCommentsByTarget_Invalid target type: {}", targetType);
            throw new AppException(ErrorCode.COMMENT_TARGET_TYPE_INVALID);
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Comment> comments = commentRepository.findByTargetTypeAndTargetIdAndParentCommentIsNull(type, targetId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<CommentResponse> getRepliesByParentComment(String parentCommentId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Comment> comments = commentRepository.findByParentCommentId(parentCommentId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<CommentResponse> getCommentsByAuthor(String authorId, PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Comment> comments = commentRepository.findByAuthorId(authorId, pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public PagingResponse<CommentResponse> getComments(PagingRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Comment> comments = commentRepository.findAll(pageable);

        return PagingResponse.<CommentResponse>builder()
                .currentPage(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElement(comments.getTotalElements())
                .data(comments.getContent().stream()
                        .map(commentMapper::toCommentResponse)
                        .toList()
                )
                .build();
    }

    @Override
    public CommentResponse updateCommentById(String commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl_updateCommentById_Comment id {} not found", commentId);
                    return new AppException(ErrorCode.COMMENT_NOT_FOUND);
                });

        comment = commentMapper.updateCommentFromRequest(comment, request);

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentById(String commentId) {
        commentRepository.deleteById(commentId);
    }
}

