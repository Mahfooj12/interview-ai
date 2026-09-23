package com.interviewai.dto.job;

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
public class JobDescriptionRequest {

    @NotBlank(message = "Job description text is required")
    @Size(min = 30, max = 40000, message = "Job description must be between 30 and 40000 characters")
    private String rawText;

    private String title;

    private String company;

    /**
     * Optional resume to immediately compute a match against.
     */
    private String resumeId;
}