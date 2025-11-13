package com.catsocute.japanlearn_hub.modules.lesson.dto.request;

import com.catsocute.japanlearn_hub.modules.lesson.constant.vocabulary.VocabularyConstants;
import com.catsocute.japanlearn_hub.modules.lesson.constant.vocabulary.VocabularyErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import com.catsocute.japanlearn_hub.modules.lesson.enums.VocabularyType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UpdateVocabularyRequest {
    @NotBlank(message = VocabularyErrorCodeConstants.VOCABULARY_WORD_INVALID)
    @Size(min = VocabularyConstants.WORD_MIN_LENGTH,
          max = VocabularyConstants.WORD_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_WORD_INVALID)
    String word;

    @NotNull(message = VocabularyErrorCodeConstants.VOCABULARY_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    VocabularyType type;

    @Size(max = VocabularyConstants.ROMAJI_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_ROMAJI_INVALID)
    String romaji;

    @Size(max = VocabularyConstants.MEANING_MAX_LENGTH,
          message = VocabularyErrorCodeConstants.VOCABULARY_MEANING_INVALID)
    String meaning;

    @NotNull(message = VocabularyErrorCodeConstants.VOCABULARY_LEVEL_INVALID)
    @Enumerated(EnumType.STRING)
    JLPTLevel level;
}
