package com.catsocute.japanlearn_hub.modules.exam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitExamRequest {
    
    @NotNull(message = "Exam ID cannot be null")
    private String examId;
    
    /**
     * Map of question ID to selected option ID(s)
     * For MULTIPLE_CHOICE and TRUE_FALSE: single option ID
     * For FILL_IN_BLANK: the option ID or null if not answered
     */
    @NotEmpty(message = "Answers cannot be empty")
    @Valid
    private Map<String, String> answers;
    
    /**
     * Time taken in seconds
     */
    @NotNull(message = "Time taken cannot be null")
    private Integer timeTakenSeconds;
}


