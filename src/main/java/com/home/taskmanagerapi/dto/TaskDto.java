package com.home.taskmanagerapi.dto;

import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * Internal representation of a Task. Immutable record with a @Builder.
 */
@Builder
public record TaskDto(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        Instant updatedAt
) {
}
