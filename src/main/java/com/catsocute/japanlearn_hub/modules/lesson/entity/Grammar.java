package com.catsocute.japanlearn_hub.modules.lesson.entity;

import com.catsocute.japanlearn_hub.common.entity.BaseEntity;
import com.catsocute.japanlearn_hub.modules.lesson.constant.grammar.GrammarConstants;
import com.catsocute.japanlearn_hub.modules.lesson.constant.grammar.GrammarErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
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
@Table(name = GrammarConstants.TABLE_GRAMMAR)
@Entity
public class Grammar extends BaseEntity {
    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_TITLE_INVALID)
    @Size(min = GrammarConstants.TITLE_MIN_LENGTH,
            max = GrammarConstants.TITLE_MAX_LENGTH,
            message = GrammarErrorCodeConstants.GRAMMAR_TITLE_INVALID)
    @Column(name = GrammarConstants.COL_TITLE,
            nullable = false,
            columnDefinition = GrammarConstants.TITLE_DEFINITION)
    String title;

    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_STRUCTURE_INVALID)
    @Size(max = GrammarConstants.STRUCTURE_MAX_LENGTH,
            message = GrammarErrorCodeConstants.GRAMMAR_STRUCTURE_INVALID)
    @Column(name = GrammarConstants.COL_STRUCTURE,
            nullable = false,
            columnDefinition = GrammarConstants.STRUCTURE_DEFINITION)
    String structure;

    @NotBlank(message = GrammarErrorCodeConstants.GRAMMAR_EXPLANATION_INVALID)
    @Size(max = GrammarConstants.EXPLANATION_MAX_LENGTH,
            message = GrammarErrorCodeConstants.GRAMMAR_EXPLANATION_INVALID)
    @Column(name = GrammarConstants.COL_EXPLANATION,
            nullable = false,
            columnDefinition = GrammarConstants.EXPLANATION_DEFINITION)
    String explanation;

    @NotNull(message = GrammarErrorCodeConstants.GRAMMAR_LEVEL_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = GrammarConstants.COL_LEVEL,
            nullable = false)
    JLPTLevel level;
}
