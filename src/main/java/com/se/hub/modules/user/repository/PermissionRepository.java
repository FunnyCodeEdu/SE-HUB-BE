package com.se.hub.modules.user.repository;

import com.se.hub.modules.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,String> {
    boolean existsByName(String name);
    Permission findByName(String name);
}
