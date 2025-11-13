package com.se.hub.modules.lesson.repository;

import com.se.hub.modules.lesson.entity.Vocabulary;
import com.se.hub.modules.lesson.enums.JLPTLevel;
import com.se.hub.modules.lesson.enums.VocabularyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, String> {
    /**
     * @param level JLPTLevel
     * @param pageable Pageable
     * @return Page<Vocabulary>
     */
    Page<Vocabulary> findByLevel(JLPTLevel level, Pageable pageable);
    
    /**
     * @param type VocabularyType
     * @param pageable Pageable
     * @return Page<Vocabulary>
     */
    Page<Vocabulary> findByType(VocabularyType type, Pageable pageable);
    
    boolean existsByWord(String word);
}
