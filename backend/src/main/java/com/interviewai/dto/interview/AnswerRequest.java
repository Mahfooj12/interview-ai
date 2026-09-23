package com.interviewai.dto.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @NotBlank(message = "Question id is required")
    private String questionId;

    @NotBlank(message = "Answer text is required")
    @Size(min = 1, max = 20000, message = "Answer must be between 1 and 20000 characters")
    private String text;

    private String audioUrl;
    private String videoUrl;
}