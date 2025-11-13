package com.se.hub.modules.lesson.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.lesson.constant.vocabulary.VocabularyConstants;
import com.se.hub.modules.lesson.constant.vocabulary.VocabularyErrorCodeConstants;
import com.se.hub.modules.lesson.enums.JLPTLevel;
import com.se.hub.modules.lesson.enums.VocabularyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = VocabularyConstants.TABLE_VOCABULARY)
@Entity
public class Vocabulary extends BaseEntity {
    @NotBlank(message = VocabularyErrorCodeConstants.VOCABULARY_WORD_INVALID)
    @NotNull(message = VocabularyErrorCodeConstants.VOCABULARY_WORD_INVALID)
    @Size(min = VocabularyConstants.WORD_MIN_LENGTH,
          max = VocabularyConstants.WORD_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_WORD_INVALID)
    @Column(name = VocabularyConstants.COL_WORD,
            nullable = false,
            columnDefinition = VocabularyConstants.WORD_DEFINITION)
    String word;

    @NotNull(message = VocabularyErrorCodeConstants.VOCABULARY_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = VocabularyConstants.COL_TYPE,
            nullable = false)
    VocabularyType type;

    @Size(max = VocabularyConstants.ROMAJI_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_ROMAJI_INVALID)
    @Column(name = VocabularyConstants.COL_ROMAJI,
            columnDefinition = VocabularyConstants.ROMAJI_DEFINITION)
    String romaji;

    @Size(max = VocabularyConstants.MEANING_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_MEANING_INVALID)
    @Column(name = VocabularyConstants.COL_MEANING,
            nullable = false,
            columnDefinition = VocabularyConstants.MEANING_DEFINITION)
    String meaning;

    @NotNull(message = VocabularyErrorCodeConstants.VOCABULARY_LEVEL_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = VocabularyConstants.COL_LEVEL,
            nullable = false)
    JLPTLevel level;
}
