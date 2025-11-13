package com.catsocute.japanlearn_hub.modules.lesson.repository;

import com.catsocute.japanlearn_hub.modules.lesson.entity.Grammar;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar,String> {
    /**
     * @param level JLPTLevel
     * @param pageable Pageable
     * @return Page<Grammar>
     */
    Page<Grammar> findByLevel(JLPTLevel level, Pageable pageable);
    boolean existsByTitle(String title);
}
