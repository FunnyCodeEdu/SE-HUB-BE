# Reaction System Documentation

## Tổng quan

Hệ thống reaction đã được đồng bộ hóa để sử dụng một entity `Reaction` chung cho tất cả các loại target (Blog, Comment, Document, Exam, Course). Thay vì sử dụng `BlogReaction` riêng biệt, tất cả reactions đều được lưu trong bảng `reaction` với cấu trúc thống nhất.

## Cấu trúc dữ liệu

### Reaction Entity

```java
@Entity
public class Reaction extends BaseEntity {
    Profile user;                    // User thực hiện reaction
    TargetType targetType;           // Loại target: BLOG, COMMENT, DOCUMENT, EXAM, COURSE
    String targetId;                 // ID của target
    ReactionType reactionType;       // Loại reaction: LIKE, DISLIKE, etc.
}
```

### ReactionInfo DTO

Response trả về cho client:

```java
public class ReactionInfo {
    Boolean userReacted;    // true nếu user đã react, false nếu chưa, null nếu chưa đăng nhập
    ReactionType type;      // Loại reaction hiện tại (LIKE, DISLIKE, etc.), null nếu chưa react
}
```

## Workflow xử lý

### 1. Get List với Reactions (Blog, Comment, Document, Exam, Course)

**Flow:**
```
1. Client request GET /api/blogs (hoặc comments, documents, exams, courses)
   ↓
2. Service query database để lấy list entities
   ↓
3. Extract tất cả IDs từ list entities
   ↓
4. Batch query reactions cho tất cả IDs cùng lúc (1 query duy nhất)
   - Query: SELECT * FROM reaction 
            WHERE targetType = ? AND targetId IN (?, ?, ...) AND userId = ?
   ↓
5. Map reactions vào Map<targetId, ReactionInfo>
   ↓
6. Map từng entity sang Response DTO và set reactions từ Map
   ↓
7. Return PagingResponse với reactions đã được populate
```

**Ví dụ code trong BlogServiceImpl:**

```java
private PagingResponse<BlogResponse> buildPagingResponse(Page<Blog> blogs) {
    String currentUserId = AuthUtils.getCurrentUserId();
    List<Blog> blogList = blogs.getContent();
    
    // Batch check reactions cho tất cả blogs (1 query duy nhất)
    List<String> blogIds = blogList.stream().map(Blog::getId).toList();
    Map<String, ReactionInfo> reactionsMap = reactionService
            .getReactionsForTargets(TargetType.BLOG, blogIds, currentUserId);
    
    return PagingResponse.<BlogResponse>builder()
            .data(blogList.stream()
                    .map(blog -> toBlogResponseWithReaction(blog, reactionsMap.get(blog.getId())))
                    .toList()
            )
            .build();
}
```

**Lợi ích:**
- Tránh N+1 query problem
- Chỉ 1 query để lấy reactions cho tất cả items trong list
- Performance tốt hơn nhiều so với query từng item

### 2. Get Single Item với Reaction

**Flow:**
```
1. Client request GET /api/blogs/{id}
   ↓
2. Service query database để lấy entity
   ↓
3. Query reaction cho entity đó (1 query)
   - Query: SELECT * FROM reaction 
            WHERE targetType = ? AND targetId = ? AND userId = ?
   ↓
4. Map entity sang Response DTO và set reaction
   ↓
5. Return Response với reaction đã được populate
```

**Ví dụ code:**

```java
public BlogResponse getById(String blogId) {
    Blog blog = blogRepository.findById(blogId)
            .orElseThrow(() -> BlogErrorCode.BLOG_NOT_FOUND.toException());
    
    String currentUserId = AuthUtils.getCurrentUserId();
    return toBlogResponseWithReaction(blog, currentUserId);
}

private BlogResponse toBlogResponseWithReaction(Blog blog, String userId) {
    BlogResponse response = blogMapper.toBlogResponse(blog);
    ReactionInfo reactionInfo = reactionService.getReactionsForTargets(
            TargetType.BLOG, 
            List.of(blog.getId()), 
            userId
    ).get(blog.getId());
    
    response.setReactions(reactionInfo != null ? reactionInfo : 
        ReactionInfo.builder().userReacted(false).type(null).build());
    return response;
}
```

### 3. Toggle Reaction (Like/Dislike)

**Flow:**
```
1. Client request POST /api/blogs/{id}/like (hoặc /reactions/BLOG/{id}?reactionType=LIKE)
   ↓
2. Service validate blog tồn tại và approved
   ↓
3. ReactionService.toggleReactionWithCount() được gọi:
   a. Query reaction hiện tại của user cho target này
   b. Nếu chưa có reaction:
      - Tạo mới reaction với reactionType = LIKE
      - Return isAdded = true
   c. Nếu đã có reaction cùng type:
      - Xóa reaction (toggle off)
      - Return isAdded = false
   d. Nếu đã có reaction khác type:
      - Update reactionType = LIKE
      - Return isAdded = true
   ↓
4. Update reaction count trong Blog entity:
   a. Query count LIKE reactions
   b. Query count DISLIKE reactions
   c. Tính totalReactionCount = likeCount - dislikeCount
   d. Update vào Blog.reactionCount và save
   ↓
5. Return BlogResponse với reaction info mới
```

**Ví dụ code trong BlogServiceImpl:**

```java
public BlogResponse likeBlog(String blogId) {
    Blog blog = blogRepository.findById(blogId)
            .orElseThrow(() -> BlogErrorCode.BLOG_NOT_FOUND.toException());
    
    // Toggle reaction
    reactionService.toggleReactionWithCount(TargetType.BLOG, blogId, ReactionType.LIKE);
    
    // Update reaction count
    updateBlogReactionCount(blog);
    
    return toBlogResponseWithReaction(blog, AuthUtils.getCurrentUserId());
}

private void updateBlogReactionCount(Blog blog) {
    long likeCount = reactionService.getReactionCount(TargetType.BLOG, blog.getId(), ReactionType.LIKE);
    long dislikeCount = reactionService.getReactionCount(TargetType.BLOG, blog.getId(), ReactionType.DISLIKE);
    int totalReactionCount = (int) (likeCount - dislikeCount);
    blog.setReactionCount(totalReactionCount);
    blogRepository.save(blog);
}
```

### 4. Remove Reaction

**Flow:**
```
1. Client request POST /api/blogs/{id}/remove-reaction
   ↓
2. Service check user đã react chưa
   ↓
3. Nếu đã react:
   a. Lấy reaction type hiện tại
   b. Toggle reaction đó để remove (toggle off)
   ↓
4. Update reaction count trong Blog entity
   ↓
5. Return BlogResponse với reaction info mới
```

**Ví dụ code:**

```java
public BlogResponse removeReaction(String blogId) {
    Blog blog = blogRepository.findById(blogId)
            .orElseThrow(() -> BlogErrorCode.BLOG_NOT_FOUND.toException());
    
    // Check và remove reaction nếu có
    boolean hasReacted = reactionService.hasUserReacted(TargetType.BLOG, blogId);
    if (hasReacted) {
        String userId = AuthUtils.getCurrentUserId();
        Map<String, ReactionInfo> reactionsMap = reactionService.getReactionsForTargets(
                TargetType.BLOG, List.of(blogId), userId
        );
        ReactionInfo currentReaction = reactionsMap.get(blogId);
        if (currentReaction != null && currentReaction.getType() != null) {
            // Toggle để remove
            reactionService.toggleReactionWithCount(TargetType.BLOG, blogId, currentReaction.getType());
        }
    }
    
    // Update reaction count
    updateBlogReactionCount(blog);
    
    return toBlogResponseWithReaction(blog, AuthUtils.getCurrentUserId());
}
```

## API Endpoints

### Unified Reaction API (ReactionController)

```
POST /api/reactions/{targetType}/{targetId}?reactionType=LIKE
- Toggle reaction cho bất kỳ target type nào
- targetType: BLOG, COMMENT, DOCUMENT, EXAM, COURSE
- reactionType: LIKE, DISLIKE, etc.

GET /api/reactions/{targetType}/{targetId}/count?reactionType=LIKE
- Lấy reaction count và status của current user
```

### Blog-specific APIs (BlogController) - Legacy support

```
POST /api/blogs/{id}/like
- Like blog (sử dụng ReactionService bên trong)

POST /api/blogs/{id}/dislike
- Dislike blog (sử dụng ReactionService bên trong)

POST /api/blogs/{id}/remove-reaction
- Remove reaction (sử dụng ReactionService bên trong)
```

## Response Format

### BlogResponse, CommentResponse, DocumentResponse, ExamResponse, CourseResponse

Tất cả đều có field `reactions`:

```json
{
  "id": "blog-id",
  "content": "...",
  "reactions": {
    "userReacted": true,
    "type": "LIKE"
  },
  ...
}
```

**Các trường hợp:**
- User đã đăng nhập và đã like: `{"userReacted": true, "type": "LIKE"}`
- User đã đăng nhập và đã dislike: `{"userReacted": true, "type": "DISLIKE"}`
- User đã đăng nhập nhưng chưa react: `{"userReacted": false, "type": null}`
- User chưa đăng nhập: `{"userReacted": false, "type": null}`

## Performance Optimization

### Batch Query Pattern

Thay vì query reaction cho từng item (N+1 problem):
```java
// ❌ BAD: N+1 queries
for (Blog blog : blogs) {
    ReactionInfo reaction = getReaction(blog.getId()); // 1 query per blog
}

// ✅ GOOD: 1 query cho tất cả
List<String> blogIds = blogs.stream().map(Blog::getId).toList();
Map<String, ReactionInfo> reactionsMap = reactionService
        .getReactionsForTargets(TargetType.BLOG, blogIds, userId); // 1 query total
```

### ReactionService.getReactionsForTargets()

```java
public Map<String, ReactionInfo> getReactionsForTargets(
        TargetType targetType, 
        List<String> targetIds, 
        String userId
) {
    if (targetIds == null || targetIds.isEmpty()) {
        return new HashMap<>();
    }
    
    // User chưa đăng nhập
    if (userId == null || userId.isBlank()) {
        return targetIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> ReactionInfo.builder()
                                .userReacted(false)
                                .type(null)
                                .build()
                ));
    }
    
    // Query batch: 1 query cho tất cả targetIds
    Profile user = profileRepository.findByUserId(userId).orElse(null);
    if (user == null) {
        return targetIds.stream()...
    }
    
    List<Reaction> reactions = reactionRepository
            .findByTargetTypeAndTargetIdInAndUser(targetType, targetIds, user);
    
    // Map reactions vào Map<targetId, ReactionInfo>
    Map<String, Reaction> reactionMap = reactions.stream()
            .collect(Collectors.toMap(Reaction::getTargetId, r -> r));
    
    // Build result map
    Map<String, ReactionInfo> result = new HashMap<>();
    for (String targetId : targetIds) {
        Reaction reaction = reactionMap.get(targetId);
        if (reaction != null) {
            result.put(targetId, ReactionInfo.builder()
                    .userReacted(true)
                    .type(reaction.getReactionType())
                    .build());
        } else {
            result.put(targetId, ReactionInfo.builder()
                    .userReacted(false)
                    .type(null)
                    .build());
        }
    }
    return result;
}
```

## Database Schema

### Reaction Table

```sql
CREATE TABLE reaction (
    id VARCHAR(36) PRIMARY KEY,
    userId VARCHAR(36) NOT NULL,
    targetType VARCHAR(20) NOT NULL,  -- BLOG, COMMENT, DOCUMENT, EXAM, COURSE
    targetId VARCHAR(36) NOT NULL,
    reactionType VARCHAR(20) NOT NULL, -- LIKE, DISLIKE, etc.
    created_by VARCHAR(255),
    create_date TIMESTAMP,
    updated_by VARCHAR(255),
    update_date TIMESTAMP,
    UNIQUE KEY uk_reaction_user_target (userId, targetType, targetId)
);
```

**Indexes:**
- `(userId, targetType, targetId)` - Unique constraint
- `(targetType, targetId)` - For counting reactions
- `(targetType, targetId, userId)` - For batch queries

## Migration từ BlogReaction

### Đã xóa:
- `BlogReaction` entity
- `BlogReactionRepository`
- Constants liên quan trong `BlogConstants`

### Migration data (nếu cần):

```sql
-- Migrate data từ blog_reaction sang reaction
INSERT INTO reaction (id, userId, targetType, targetId, reactionType, created_by, create_date, updated_by, update_date)
SELECT 
    UUID() as id,
    br.user_id as userId,
    'BLOG' as targetType,
    br.blog_id as targetId,
    CASE 
        WHEN br.is_like = true THEN 'LIKE'
        WHEN br.is_like = false THEN 'DISLIKE'
    END as reactionType,
    br.created_by,
    br.create_date,
    br.updated_by,
    br.update_date
FROM blog_reactions br;
```

## Lưu ý quan trọng

1. **Reaction Count**: Blog vẫn giữ field `reactionCount` để cache, được tính từ `likeCount - dislikeCount` mỗi khi có thay đổi reaction.

2. **Transaction**: Tất cả operations đều được wrap trong `@Transactional` để đảm bảo data consistency.

3. **Virtual Thread**: Code sử dụng synchronous blocking I/O, virtual threads tự động handle để đạt high concurrency.

4. **Error Handling**: 
   - User chưa đăng nhập: `userReacted = false`, `type = null`
   - Profile không tồn tại: Return default `userReacted = false`
   - Exception trong batch query: Return default cho tất cả items

5. **Backward Compatibility**: BlogController vẫn giữ các endpoints `/like`, `/dislike`, `/remove-reaction` để không break existing clients, nhưng bên trong đã dùng ReactionService.

## Testing

### Test Cases cần cover:

1. **Get list với reactions:**
   - List có reactions
   - List không có reactions
   - User chưa đăng nhập
   - Empty list

2. **Toggle reaction:**
   - Like khi chưa có reaction
   - Like khi đã dislike
   - Unlike (toggle off)
   - Dislike khi đã like

3. **Batch query:**
   - Query nhiều items cùng lúc
   - Performance test với 100+ items

4. **Reaction count:**
   - Count đúng sau khi like/dislike
   - Count đúng sau khi remove

