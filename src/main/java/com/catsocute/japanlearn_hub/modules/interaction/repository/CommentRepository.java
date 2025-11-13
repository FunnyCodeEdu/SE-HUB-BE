package com.catsocute.japanlearn_hub.modules.interaction.repository;

import com.catsocute.japanlearn_hub.modules.interaction.entity.Comment;
import com.catsocute.japanlearn_hub.modules.interaction.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * Find comments by author
     * @param authorId String
     * @param pageable Pageable
     * @return Page<Comment>
     */
    @Query("""
        SELECT c FROM Comment c
        WHERE c.author.id = :authorId
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
}

