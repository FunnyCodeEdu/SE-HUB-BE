package com.se.hub.modules.blog.repository;

import com.se.hub.modules.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {
    Page<Blog> findAllByAuthor_Id(String authorId, Pageable pageable);
}
