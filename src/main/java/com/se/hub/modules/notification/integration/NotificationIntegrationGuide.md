# Notification Module Integration Guide

Hướng dẫn tích hợp Notification Module với các Business Modules khác.

## Nguyên tắc

- **Các Business Modules KHÔNG tự gửi notification**, chỉ **emit Events**
- **Notification Module duy nhất** xử lý tất cả notifications
- Sử dụng `ApplicationEventPublisher` để emit events

## Cách tích hợp

### 1. Inject ApplicationEventPublisher

```java
@RequiredArgsConstructor
public class YourService {
    private final ApplicationEventPublisher eventPublisher;
    
    // ... your code
}
```

### 2. Emit Events sau khi xử lý business logic

```java
// Sau khi xử lý thành công
eventPublisher.publishEvent(new YourEvent(this, param1, param2, ...));
```

## Ví dụ tích hợp

### BlogModule - Emit BlogApprovedEvent

Khi blog được duyệt (approve), emit event:

```java
import com.se.hub.modules.notification.event.BlogApprovedEvent;
import org.springframework.context.ApplicationEventPublisher;

// Trong BlogService hoặc BlogModerationService
@RequiredArgsConstructor
public class BlogModerationService {
    private final ApplicationEventPublisher eventPublisher;
    private final BlogRepository blogRepository;
    
    public void approveBlog(String blogId) {
        Blog blog = blogRepository.findById(blogId)
            .orElseThrow(() -> new BlogException(BlogErrorCode.BLOG_NOT_FOUND));
        
        // Business logic: approve blog
        blog.setStatus(BlogStatus.APPROVED);
        blogRepository.save(blog);
        
        // Emit event for notification
        BlogApprovedEvent event = new BlogApprovedEvent(
            this,
            blog.getAuthor().getUser().getId(),
            blog.getId(),
            blog.getContent().substring(0, Math.min(50, blog.getContent().length())) // title
        );
        eventPublisher.publishEvent(event);
    }
}
```

### InteractionModule - Emit PostLikedEvent và MentionEvent

#### PostLikedEvent khi có like:

```java
import com.se.hub.modules.notification.event.PostLikedEvent;
import org.springframework.context.ApplicationEventPublisher;

// Trong ReactionService hoặc LikeService
@RequiredArgsConstructor
public class ReactionService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void likePost(String targetType, String targetId) {
        // Business logic: save like
        // ...
        
        // Get post owner
        String postOwnerUserId = getPostOwner(targetType, targetId);
        String likerUserId = AuthUtils.getCurrentUserId();
        String postTitle = getPostTitle(targetType, targetId);
        
        // Emit event
        PostLikedEvent event = new PostLikedEvent(
            this,
            postOwnerUserId,
            likerUserId,
            targetType,
            targetId,
            postTitle
        );
        eventPublisher.publishEvent(event);
    }
}
```

#### MentionEvent khi có mention trong comment:

```java
import com.se.hub.modules.notification.event.MentionEvent;
import org.springframework.context.ApplicationEventPublisher;

// Trong CommentServiceImpl
@RequiredArgsConstructor
public class CommentServiceImpl {
    private final ApplicationEventPublisher eventPublisher;
    
    public CommentResponse createComment(CreateCommentRequest request) {
        // Business logic: save comment
        Comment comment = commentMapper.toComment(request);
        comment = commentRepository.save(comment);
        
        // Detect mentions in comment content
        List<String> mentionedUserIds = detectMentions(request.getContent());
        
        // Emit MentionEvent for each mentioned user
        for (String mentionedUserId : mentionedUserIds) {
            MentionEvent event = new MentionEvent(
                this,
                mentionedUserId,
                AuthUtils.getCurrentUserId(),
                comment.getId(),
                comment.getContent(),
                request.getTargetType(),
                request.getTargetId()
            );
            eventPublisher.publishEvent(event);
        }
        
        return commentMapper.toCommentResponse(comment);
    }
    
    private List<String> detectMentions(String content) {
        // Implement mention detection logic
        // e.g., find @username patterns
        // Return list of user IDs
        return Collections.emptyList();
    }
}
```

### ProfileModule - Emit AchievementUnlockedEvent

```java
import com.se.hub.modules.notification.event.AchievementUnlockedEvent;
import org.springframework.context.ApplicationEventPublisher;

// Trong AchievementService
@RequiredArgsConstructor
public class AchievementService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void unlockAchievement(String userId, String achievementId) {
        // Business logic: unlock achievement
        // ...
        
        Achievement achievement = achievementRepository.findById(achievementId)
            .orElseThrow(() -> new AchievementException(...));
        
        // Emit event
        AchievementUnlockedEvent event = new AchievementUnlockedEvent(
            this,
            userId,
            achievementId,
            achievement.getName(),
            achievement.getDescription()
        );
        eventPublisher.publishEvent(event);
    }
}
```

## Lưu ý

1. **Luôn emit events SAU khi business logic thành công**
2. **Events phải là async** - Notification Module sẽ xử lý async
3. **Không block business logic** để chờ notification
4. **Events phải chứa đủ thông tin** để tạo notification


