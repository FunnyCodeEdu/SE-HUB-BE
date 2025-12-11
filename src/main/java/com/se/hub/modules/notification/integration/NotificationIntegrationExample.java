package com.se.hub.modules.notification.integration;

import com.se.hub.modules.notification.event.BlogApprovedEvent;
import com.se.hub.modules.notification.event.MentionEvent;
import com.se.hub.modules.notification.event.PostLikedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Example integration code for emitting notification events

 * This file shows how to integrate Notification Module with Business Modules.
 * Copy the relevant code snippets to your service implementations.

 * IMPORTANT: This is EXAMPLE CODE - integrate into your actual services!
 */
@Component
public class NotificationIntegrationExample {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public NotificationIntegrationExample(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * EXAMPLE: Emit BlogApprovedEvent when blog is approved

     * Add this to BlogService or BlogModerationService:

     * RequiredArgsConstructor
     * public class BlogModerationService {
     *     private final ApplicationEventPublisher eventPublisher;

     *     public void approveBlog(String blogId) {
     *         Blog blog = blogRepository.findById(blogId)...

     *         // Business logic: approve blog
     *         blog.setStatus(BlogStatus.APPROVED);
     *         blogRepository.save(blog);

     *         // Emit event for notification
     *         BlogApprovedEvent event = new BlogApprovedEvent(
     *             this,
     *             blog.getAuthor().getUser().getId(),
     *             blog.getId(),
     *             extractTitle(blog.getContent())
     *         );
     *         eventPublisher.publishEvent(event);
     *     }
     * }
     */
    public void exampleApproveBlog(String blogAuthorUserId, String blogId, String blogTitle) {
        BlogApprovedEvent event = new BlogApprovedEvent(
            this,
            blogAuthorUserId,
            blogId,
            blogTitle
        );
        eventPublisher.publishEvent(event);
    }
    
    /**
     * EXAMPLE: Emit PostLikedEvent when post/blog is liked

     * Add this to ReactionService or LikeService:

     * public void likePost(String targetType, String targetId) {
     *     // Business logic: save like
     *     ...

     *     // Get post owner and emit event
     *     String postOwnerUserId = getPostOwner(targetType, targetId);
     *     String likerUserId = AuthUtils.getCurrentUserId();
     *     String postTitle = getPostTitle(targetType, targetId);

     *     PostLikedEvent event = new PostLikedEvent(
     *         this,
     *         postOwnerUserId,
     *         likerUserId,
     *         targetType,
     *         targetId,
     *         postTitle
     *     );
     *     eventPublisher.publishEvent(event);
     * }
     */
    public void exampleLikePost(String postOwnerUserId, String likerUserId, 
                               String targetType, String targetId, String targetTitle) {
        PostLikedEvent event = new PostLikedEvent(
            this,
            postOwnerUserId,
            likerUserId,
            targetType,
            targetId,
            targetTitle
        );
        eventPublisher.publishEvent(event);
    }
    
    /**
     * EXAMPLE: Emit MentionEvent when user is mentioned in comment

     * Add this to CommentServiceImpl.createComment():

     * public CommentResponse createComment(CreateCommentRequest request) {
     *     // Business logic: save comment
     *     Comment comment = commentRepository.save(...);

     *     // Detect mentions and emit events
     *     List<String> mentionedUserIds = detectMentions(request.getContent());
     *     for (String mentionedUserId : mentionedUserIds) {
     *         MentionEvent event = new MentionEvent(
     *             this,
     *             mentionedUserId,
     *             AuthUtils.getCurrentUserId(),
     *             comment.getId(),
     *             comment.getContent(),
     *             request.getTargetType().name(),
     *             request.getTargetId()
     *         );
     *         eventPublisher.publishEvent(event);
     *     }

     *     return commentMapper.toCommentResponse(comment);
     * }
     */
    public void exampleMentionInComment(String mentionedUserId, String mentionerUserId,
                                       String commentId, String commentContent,
                                       String targetType, String targetId) {
        MentionEvent event = new MentionEvent(
            this,
            mentionedUserId,
            mentionerUserId,
            commentId,
            commentContent,
            targetType,
            targetId
        );
        eventPublisher.publishEvent(event);
    }
}



