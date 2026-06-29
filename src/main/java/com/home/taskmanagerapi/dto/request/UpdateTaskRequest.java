package com.home.taskmanagerapi.dto.request;

import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Fields for a full update of a task")
public record UpdateTaskRequest(

        @Schema(description = "Short summary of the task", example = "Write API documentation")
        @NotBlank @Size(min = 3, max = 255)
        String title,

        @Schema(description = "Optional longer details", example = "Cover all CRUD endpoints and error codes")
        @Size(max = 1000)
        String description,

        @Schema(description = "Lifecycle status", example = "IN_PROGRESS")
        @NotNull
        TaskStatus status,

        @Schema(description = "Task priority", example = "HIGH")
        @NotNull
        TaskPriority priority
) {
}