package com.interviewai.service.impl;

import com.interviewai.ai.speech.SpeechToTextClient;
import com.interviewai.ai.speech.TextToSpeechClient;
import com.interviewai.ai.speech.dto.SttRequest;
import com.interviewai.ai.speech.dto.SttResponse;
import com.interviewai.ai.speech.dto.TtsRequest;
import com.interviewai.ai.speech.dto.TtsResponse;
import com.interviewai.dto.voice.SpeechSynthesizeRequest;
import com.interviewai.dto.voice.SpeechTranscribeResponse;
import com.interviewai.exception.BadRequestException;
import com.interviewai.service.VoiceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VoiceServiceImpl implements VoiceService {

    private final SpeechToTextClient sttClient;
    private final TextToSpeechClient ttsClient;

    public VoiceServiceImpl(SpeechToTextClient sttClient, TextToSpeechClient ttsClient) {
        this.sttClient = sttClient;
        this.ttsClient = ttsClient;
    }

    @Override
    public SpeechTranscribeResponse transcribe(MultipartFile audio, String language) {
        if (audio == null || audio.isEmpty()) {
            throw new BadRequestException("Audio file is empty");
        }
        try {
            SttResponse result = sttClient.transcribe(SttRequest.builder()
                    .audio(audio.getBytes())
                    .fileName(audio.getOriginalFilename())
                    .mimeType(audio.getContentType())
                    .language(language)
                    .build());

            return SpeechTranscribeResponse.builder()
                    .transcript(result.getTranscript())
                    .durationMs(result.getDurationMs())
                    .confidence(result.getConfidence())
                    .build();
        } catch (IOException e) {
            throw new BadRequestException("Failed to read audio payload: " + e.getMessage());
        }
    }

    @Override
    public SynthesizedAudio synthesize(SpeechSynthesizeRequest request) {
        TtsResponse response = ttsClient.synthesize(TtsRequest.builder()
                .text(request.getText())
                .voice(request.getVoice())
                .build());
        return new SynthesizedAudio(response.getAudio(), response.getMimeType());
    }
}