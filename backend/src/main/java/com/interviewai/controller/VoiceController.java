package com.interviewai.controller;

import com.interviewai.dto.voice.SpeechSynthesizeRequest;
import com.interviewai.dto.voice.SpeechTranscribeResponse;
import com.interviewai.exception.ApiResponse;
import com.interviewai.security.CustomUserDetails;
import com.interviewai.service.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
@Tag(name = "Voice", description = "Speech-to-text and text-to-speech")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @PostMapping(path = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Transcribe an audio recording to text")
    public ResponseEntity<ApiResponse<SpeechTranscribeResponse>> transcribe(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "language", required = false) String language) {
        SpeechTranscribeResponse response = voiceService.transcribe(audio, language);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping(path = "/synthesize", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Synthesize speech from text and return MP3 audio")
    public ResponseEntity<byte[]> synthesize(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody SpeechSynthesizeRequest request) {
        VoiceService.SynthesizedAudio audio = voiceService.synthesize(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, audio.mimeType())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(audio.bytes());
    }
}
