package com.interviewai.ai.speech;

import com.interviewai.ai.speech.dto.TtsRequest;
import com.interviewai.ai.speech.dto.TtsResponse;

public interface TextToSpeechClient {

    /**
     * Synthesize text into speech audio bytes.
     */
    TtsResponse synthesize(TtsRequest request);

    /**
     * Provider identifier, e.g. "openai", "elevenlabs".
     */
    String provider();
}
