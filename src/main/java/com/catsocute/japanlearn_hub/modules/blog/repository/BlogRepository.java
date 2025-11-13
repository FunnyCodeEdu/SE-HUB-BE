package com.catsocute.japanlearn_hub.modules.blog.repository;

import com.catsocute.japanlearn_hub.modules.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {
    Page<Blog> findAllByAuthor_Id(String authorId, Pageable pageable);
}
