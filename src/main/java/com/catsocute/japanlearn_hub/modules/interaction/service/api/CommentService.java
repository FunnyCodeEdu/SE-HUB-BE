package com.catsocute.japanlearn_hub.modules.interaction.service.api;

import com.catsocute.japanlearn_hub.common.dto.request.PagingRequest;
import com.catsocute.japanlearn_hub.common.dto.response.PagingResponse;
import com.catsocute.japanlearn_hub.modules.interaction.dto.request.CreateCommentRequest;
import com.catsocute.japanlearn_hub.modules.interaction.dto.request.UpdateCommentRequest;
import com.catsocute.japanlearn_hub.modules.interaction.dto.response.CommentResponse;

public interface CommentService {
    
    /**
     * create new comment
     * @author catsocute
     */
    CommentResponse createComment(CreateCommentRequest request);
    
    /**
     * get comment by id
     * @author catsocute
     */
    CommentResponse getById(String commentId);
    
    /**
     * get comments by target
     * @author catsocute
     */
    PagingResponse<CommentResponse> getCommentsByTarget(String targetType, String targetId, PagingRequest request);
    
    /**
     * get parent comments by target (exclude replies)
     * @author catsocute
     */
    PagingResponse<CommentResponse> getParentCommentsByTarget(String targetType, String targetId, PagingRequest request);
    
    /**
     * get replies for a comment
     * @author catsocute
     */
    PagingResponse<CommentResponse> getRepliesByParentComment(String parentCommentId, PagingRequest request);
    
    /**
     * get comments by author
     * @author catsocute
     */
    PagingResponse<CommentResponse> getCommentsByAuthor(String authorId, PagingRequest request);
    
    /**
     * get all comments
     * @author catsocute
     */
    PagingResponse<CommentResponse> getComments(PagingRequest request);
    
    /**
     * update comment by id
     * @author catsocute
     */
    CommentResponse updateCommentById(String commentId, UpdateCommentRequest request);
    
    /**
     * delete comment by id
     * @author catsocute
     */
    void deleteCommentById(String commentId);
}

