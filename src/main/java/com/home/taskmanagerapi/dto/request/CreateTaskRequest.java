package com.home.taskmanagerapi.dto.request;

import com.home.taskmanagerapi.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Fields required to create a task")
public record CreateTaskRequest(

        @Schema(description = "Short summary of the task", example = "Write API documentation")
        @NotBlank @Size(min = 3, max = 255)
        String title,

        @Schema(description = "Optional longer details", example = "Cover all CRUD endpoints and error codes")
        @Size(max = 1000)
        String description,

        @Schema(description = "Task priority", example = "MEDIUM")
        @NotNull
        TaskPriority priority
) {
}
