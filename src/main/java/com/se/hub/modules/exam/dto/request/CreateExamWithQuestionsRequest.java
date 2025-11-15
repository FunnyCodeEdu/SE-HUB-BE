package com.se.hub.modules.exam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateExamWithQuestionsRequest {
    @Valid
    CreateExamRequest exam;
    
    @NotEmpty(message = "Questions list cannot be empty")
    @Valid
    List<CreateQuestionRequest> questions;
}

