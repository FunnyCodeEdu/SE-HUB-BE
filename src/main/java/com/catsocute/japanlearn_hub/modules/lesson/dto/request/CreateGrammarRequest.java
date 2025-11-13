package com.catsocute.japanlearn_hub.modules.lesson.dto.request;

import com.catsocute.japanlearn_hub.modules.lesson.constant.grammar.GrammarConstants;
import com.catsocute.japanlearn_hub.modules.lesson.constant.grammar.GrammarErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
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
public class CreateGrammarRequest {
    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_TITLE_INVALID)
    @NotNull(message = GrammarErrorCodeConstants.GRAMMAR_TITLE_INVALID)
    @Size(min = GrammarConstants.TITLE_MIN_LENGTH,
          max = GrammarConstants.TITLE_MAX_LENGTH,
          message = GrammarErrorCodeConstants.GRAMMAR_TITLE_INVALID)
    String title;

    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_STRUCTURE_INVALID)
    @NotNull(message = GrammarErrorCodeConstants.GRAMMAR_STRUCTURE_INVALID)
    @Size(max = GrammarConstants.STRUCTURE_MAX_LENGTH,
          message = GrammarErrorCodeConstants.GRAMMAR_STRUCTURE_INVALID)
    String structure;

    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_EXPLANATION_INVALID)
    @NotNull(message = GrammarErrorCodeConstants.GRAMMAR_EXPLANATION_INVALID)
    @Size(max = GrammarConstants.EXPLANATION_MAX_LENGTH,
          message = GrammarErrorCodeConstants.GRAMMAR_EXPLANATION_INVALID)
    String explanation;

    @NotNull(message = GrammarErrorCodeConstants.GRAMMAR_LEVEL_INVALID)
    @Enumerated(EnumType.STRING)
    JLPTLevel level;
}