package com.interviewai.ai.speech.dto;

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
public class TtsResponse {
    private byte[] audio;
    private String mimeType;
    private long durationMs;
}
