package com.catsocute.japanlearn_hub.modules.course.repository;

import com.catsocute.japanlearn_hub.modules.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {
    boolean existsByName(String name);
    Page<Course> findAllByUsers_Id(String userId,  Pageable pageable);
}
