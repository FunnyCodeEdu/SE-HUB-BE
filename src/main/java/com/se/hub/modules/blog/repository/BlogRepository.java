package com.se.hub.modules.blog.repository;

import com.se.hub.modules.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String>, JpaSpecificationExecutor<Blog> {
    Page<Blog> findAllByAuthor_Id(String authorId, Pageable pageable);
    
    /**
     * Find all approved blogs by author ID
     */
    @Query("SELECT b FROM Blog b WHERE b.author.id = :authorId AND b.isApproved = true")
    Page<Blog> findAllApprovedByAuthor_Id(@Param("authorId") String authorId, Pageable pageable);
    
    /**
     * Find all approved blogs
     */
    @Query("SELECT b FROM Blog b WHERE b.isApproved = true")
    Page<Blog> findAllApproved(Pageable pageable);

    /**
     * Find most popular blogs sorted by view count (only approved)
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author WHERE b.isApproved = true ORDER BY b.viewCount DESC")
    Page<Blog> findMostPopularBlogs(Pageable pageable);

    /**
     * Find most liked blogs sorted by reaction count (only approved)
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author WHERE b.isApproved = true ORDER BY b.reactionCount DESC")
    Page<Blog> findMostLikedBlogs(Pageable pageable);

    /**
     * Find latest blogs sorted by created date (only approved)
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author WHERE b.isApproved = true ORDER BY b.createDate DESC")
    Page<Blog> findLatestBlogs(Pageable pageable);
    
    @Query(value = """
            SELECT DISTINCT b FROM Blog b
            LEFT JOIN FETCH b.author a
            WHERE b.isApproved = true AND (
                LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(a.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """,
            countQuery = """
            SELECT COUNT(DISTINCT b.id) FROM Blog b
            LEFT JOIN b.author a
            WHERE b.isApproved = true AND (
                LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(a.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Blog> searchApprovedBlogs(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Find all pending blogs (not approved)
     */
    Page<Blog> findAllByIsApprovedFalse(Pageable pageable);

    /**
     * Find top N latest blogs (only approved)
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author WHERE b.isApproved = true ORDER BY b.createDate DESC")
    List<Blog> findTopNByOrderByCreatedDateDesc(Pageable pageable);

    /**
     * Atomic operation to increment view count
     * Prevents race conditions when multiple requests update view count simultaneously
     */
    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :blogId")
    void incrementViewCount(@Param("blogId") String blogId);

    /**
     * Get view counts for multiple blog IDs
     * Used to refresh view counts from database when building cached responses
     */
    @Query("SELECT b.id, b.viewCount FROM Blog b WHERE b.id IN :blogIds")
    List<Object[]> findViewCountsByIds(@Param("blogIds") List<String> blogIds);

    /**
     * Atomic operation to increment reaction count by delta.
     * Delta can be positive (like) or negative (unlike)
     * Prevents race conditions when multiple requests update reaction count simultaneously
     */
    @Modifying
    @Query("UPDATE Blog b SET b.reactionCount = b.reactionCount + :delta WHERE b.id = :blogId")
    void incrementReactionCount(@Param("blogId") String blogId, @Param("delta") int delta);

    /**
     * Atomic operation to increment comment count by delta
     * Delta can be positive (add comment) or negative (delete comment)
     * Prevents race conditions when multiple requests update comment count simultaneously
     */
    @Modifying
    @Query("UPDATE Blog b SET b.cmtCount = b.cmtCount + :delta WHERE b.id = :blogId")
    void incrementCommentCount(@Param("blogId") String blogId, @Param("delta") int delta);
}
