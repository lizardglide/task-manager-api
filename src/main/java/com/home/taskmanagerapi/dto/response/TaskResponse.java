package com.home.taskmanagerapi.dto.response;

import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "A task representation returned to clients")
public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        Instant updatedAt
) {
}
