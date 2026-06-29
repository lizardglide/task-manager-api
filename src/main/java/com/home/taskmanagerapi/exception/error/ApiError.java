package com.home.taskmanagerapi.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

/**
 * Client-facing error body. Minimal by design: a generic message plus a
 * correlation id, full detail stays in the server logs under that traceId.
 */
@Builder
@Schema(description = "Standard error response returned to the client")
public record ApiError(

        @Schema(description = "When the error occurred", example = "2026-06-28T10:15:30Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Generic, client-safe message", example = "Invalid request")
        String error,

        @Schema(description = "Correlation id that matches the server log",
                example = "1c3d8a2e-9b0f-4f7e-8d2a-2b1c9e7f4a55")
        String traceId
) {
}
