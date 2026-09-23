package com.interviewai.dto.voice;

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
public class SpeechSynthesizeRequest {

    @NotBlank(message = "Text is required")
    @Size(max = 4000, message = "Text is too long")
    private String text;

    private String voice;
}