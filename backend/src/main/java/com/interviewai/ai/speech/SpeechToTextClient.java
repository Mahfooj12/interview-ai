package com.interviewai.ai.speech;

import com.interviewai.ai.speech.dto.SttRequest;
import com.interviewai.ai.speech.dto.SttResponse;

public interface SpeechToTextClient {

    /**
     * Transcribe an audio payload to plain text.
     */
    SttResponse transcribe(SttRequest request);

    /**
     * Provider identifier, e.g. "deepgram", "whisper", "openai".
     */
    String provider();
}
