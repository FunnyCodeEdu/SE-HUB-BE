package com.catsocute.japanlearn_hub.modules.user.repository;

import com.catsocute.japanlearn_hub.modules.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
