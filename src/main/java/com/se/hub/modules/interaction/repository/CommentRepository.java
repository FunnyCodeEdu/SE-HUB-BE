package com.se.hub.modules.interaction.repository;

import com.se.hub.modules.interaction.entity.Comment;
import com.se.hub.modules.interaction.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    
    /**
     * Find comments by target type and target ID
     * @param targetType TargetType
     * @param targetId String
     * @param pageable Pageable
     * @return Page<Comment>
     */
    Page<Comment> findByTargetTypeAndTargetId(TargetType targetType, String targetId, Pageable pageable);
    
    /**
     * Find parent comments (comments without parent) by target type and target ID
     * @param targetType TargetType
     * @param targetId String
     * @param pageable Pageable
     * @return Page<Comment>
     */
    Page<Comment> findByTargetTypeAndTargetIdAndParentCommentIsNull(TargetType targetType, String targetId, Pageable pageable);
    
    /**
     * Find replies for a parent comment
     * @param parentCommentId String
     * @param pageable Pageable
     * @return Page<Comment>
     */
    Page<Comment> findByParentCommentId(String parentCommentId, Pageable pageable);
    
    /**
     * Find comments by author (User ID)
     * @param authorId String (User ID)
     * @param pageable Pageable
     * @return Page<Comment>
     */
    @Query("""
        SELECT c FROM Comment c
        WHERE c.author.user.id = :authorId
        """)
    Page<Comment> findByAuthorId(@Param("authorId") String authorId, Pageable pageable);
    
    /**
     * Count comments by target type and target ID
     * @param targetType TargetType
     * @param targetId String
     * @return long
     */
    long countByTargetTypeAndTargetId(TargetType targetType, String targetId);
    
    /**
     * Count replies by parent comment ID
     * @param parentCommentId String
     * @return long
     */
    long countByParentCommentId(String parentCommentId);

    /**
     * Batch count comments by target type and target IDs
     * Returns map of targetId -> count
     */
    @Query("SELECT c.targetId, COUNT(c) FROM Comment c " +
           "WHERE c.targetType = :targetType AND c.targetId IN :targetIds " +
           "GROUP BY c.targetId")
    List<Object[]> countByTargetTypeAndTargetIdIn(
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<String> targetIds);
}

