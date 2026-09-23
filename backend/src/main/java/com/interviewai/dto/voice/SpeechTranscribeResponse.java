package com.interviewai.dto.voice;

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
public class SpeechTranscribeResponse {
    private String transcript;
    private long durationMs;
    private Double confidence;
}