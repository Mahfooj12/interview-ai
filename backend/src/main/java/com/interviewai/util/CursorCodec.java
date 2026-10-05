package com.interviewai.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewai.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

/**
 * Handles encoding/decoding of opaque cursors used for pagination.
 *
 * Cursor format (decoded):
 *   { "startedAt": "2026-09-24T10:15:00Z", "id": "66f1234abcd" }
 *
 * Wire format: URL-safe Base64 of the JSON above.
 *   eyJzdGFydGVkQXQiOiIyMDI2LTA5LTI0VDEwOjE1OjAwWiIsImlkIjoiNjZmMTIzNGFiY2QifQ
 *
 * Design notes:
 * - Opaque to clients — they must not parse it.
 * - Uses URL-safe Base64 so it can be passed as a query parameter without escaping.
 * - Contains both startedAt and id because startedAt alone is not unique.
 */
@Component
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public CursorCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record DecodedCursor(Instant startedAt, String id) {}

    /**
     * Encode a cursor from its components.
     */
    public String encode(Instant startedAt, String id) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "startedAt", startedAt.toString(),
                    "id", id
            ));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode cursor", e);
        }
    }

    /**
     * Decode a cursor string. Throws BadRequestException if the input is
     * not a valid cursor (malformed Base64, invalid JSON, missing fields).
     */
    public DecodedCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new BadRequestException("Cursor is empty");
        }
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);

            Map<String, Object> map = objectMapper.readValue(
                    json, new TypeReference<Map<String, Object>>() {}
            );

            Object startedAtRaw = map.get("startedAt");
            Object idRaw = map.get("id");
            if (startedAtRaw == null || idRaw == null) {
                throw new BadRequestException("Cursor is missing required fields");
            }

            Instant startedAt = Instant.parse(startedAtRaw.toString());
            String id = idRaw.toString();
            return new DecodedCursor(startedAt, id);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid cursor format");
        }
    }
}
