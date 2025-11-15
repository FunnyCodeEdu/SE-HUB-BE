package com.se.hub.modules.blog.repository;

import com.se.hub.modules.blog.entity.BlogReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogReactionRepository extends JpaRepository<BlogReaction, String> {
    Optional<BlogReaction> findByBlog_IdAndUser_Id(String blogId, String userId);
    
    boolean existsByBlog_IdAndUser_Id(String blogId, String userId);
    
    @Query("SELECT COUNT(br) FROM BlogReaction br WHERE br.blog.id = :blogId AND br.isLike = true")
    long countLikesByBlogId(@Param("blogId") String blogId);
    
    @Query("SELECT COUNT(br) FROM BlogReaction br WHERE br.blog.id = :blogId AND br.isLike = false")
    long countDislikesByBlogId(@Param("blogId") String blogId);
}

