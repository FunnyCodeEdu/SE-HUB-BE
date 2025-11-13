package com.catsocute.japanlearn_hub.modules.lesson.repository;

import com.catsocute.japanlearn_hub.modules.lesson.entity.Lesson;
import com.catsocute.japanlearn_hub.modules.lesson.enums.LessonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    /**
     * @param type LessonType
     * @param pageable Pageable
     * @return Page<Lesson>
     */
    Page<Lesson> findByType(LessonType type, Pageable pageable);
    
    /**
     * @param courseId String
     * @param pageable Pageable
     * @return Page<Lesson>
     */
    Page<Lesson> findByCourseId(String courseId, Pageable pageable);
    
    /**
     * @param parentLessonId String
     * @param pageable Pageable
     * @return Page<Lesson>
     */
    Page<Lesson> findByParentLessonId(String parentLessonId, Pageable pageable);
    
    boolean existsByTitle(String title);
}
