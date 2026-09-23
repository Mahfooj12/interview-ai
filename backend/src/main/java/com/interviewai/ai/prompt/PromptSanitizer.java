package com.interviewai.ai.prompt;

import org.springframework.stereotype.Component;

/**
 * Protects against prompt injection attacks embedded in user-supplied content
 * (resumes, job descriptions, answers).
 */
@Component
public class PromptSanitizer {

    private static final int MAX_INPUT_LENGTH = 60_000;

    private static final String[] INJECTION_MARKERS = {
            "ignore previous instructions",
            "ignore all previous",
            "disregard previous",
            "forget previous",
            "system prompt",
            "you are now",
            "act as",
            "override instructions",
            "new instructions",
            "jailbreak",
            "developer mode"
    };

    /**
     * Wrap untrusted content in delimiters and cap length.
     * Removes control characters to avoid hidden injection.
     */
    public String wrap(String label, String raw) {
        if (raw == null) return "";
        String cleaned = raw
                .replaceAll("[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u001F]", " ")
                .trim();
        if (cleaned.length() > MAX_INPUT_LENGTH) {
            cleaned = cleaned.substring(0, MAX_INPUT_LENGTH);
        }
        return "<<<BEGIN_" + label + ">>>\n" + cleaned + "\n<<<END_" + label + ">>>";
    }

    /**
     * Detects suspicious prompt-injection markers. Used only for logging/monitoring.
     */
    public boolean looksLikeInjection(String raw) {
        if (raw == null) return false;
        String lower = raw.toLowerCase();
        for (String marker : INJECTION_MARKERS) {
            if (lower.contains(marker)) return true;
        }
        return false;
    }
}
