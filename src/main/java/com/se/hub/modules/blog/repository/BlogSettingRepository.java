package com.se.hub.modules.blog.repository;

import com.mongodb.lang.NonNull;
import com.se.hub.modules.blog.entity.BlogSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogSettingRepository extends JpaRepository<BlogSetting, String> {
    Optional<BlogSetting> findById(@NonNull String id);
}

