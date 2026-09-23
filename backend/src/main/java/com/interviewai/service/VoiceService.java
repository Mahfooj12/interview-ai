package com.interviewai.service;

import com.interviewai.dto.voice.SpeechSynthesizeRequest;
import com.interviewai.dto.voice.SpeechTranscribeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {

    SpeechTranscribeResponse transcribe(MultipartFile audio, String language);

    /**
     * Returns synthesized audio bytes plus MIME type.
     */
    SynthesizedAudio synthesize(SpeechSynthesizeRequest request);

    record SynthesizedAudio(byte[] bytes, String mimeType) {}
}
