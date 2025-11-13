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
     * Find most popular blogs sorted by view count
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author ORDER BY b.viewCount DESC")
    Page<Blog> findMostPopularBlogs(Pageable pageable);

    /**
     * Find most liked blogs sorted by reaction count
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author ORDER BY b.reactionCount DESC")
    Page<Blog> findMostLikedBlogs(Pageable pageable);

    /**
     * Find latest blogs sorted by created date
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author ORDER BY b.createDate DESC")
    Page<Blog> findLatestBlogs(Pageable pageable);

    /**
     * Find top N latest blogs
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading author
     */
    @Query("SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.author ORDER BY b.createDate DESC")
    List<Blog> findTopNByOrderByCreatedDateDesc(Pageable pageable);

    /**
     * Atomic operation to increment view count
     * Prevents race conditions when multiple requests update view count simultaneously
     */
    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :blogId")
    void incrementViewCount(@Param("blogId") String blogId);

    /**
     * Atomic operation to increment reaction count by delta
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
