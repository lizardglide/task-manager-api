package com.home.taskmanagerapi.controller;

import com.home.taskmanagerapi.dto.request.CreateTaskRequest;
import com.home.taskmanagerapi.dto.request.UpdateTaskRequest;
import com.home.taskmanagerapi.dto.response.TaskResponse;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import com.home.taskmanagerapi.exception.error.ApiError;
import com.home.taskmanagerapi.mapper.TaskMapper;
import com.home.taskmanagerapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Create, read, update, delete and filter tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping
    @Operation(summary = "Create a task", description = "Creates a new task; it always starts in OPEN status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        var created = taskService.create(taskMapper.toDto(request));
        var body = taskMapper.toResponse(created);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TaskResponse getById(@PathVariable UUID id) {
        return taskMapper.toResponse(taskService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List tasks",
            description = "Returns a page of tasks, optionally filtered by status and/or priority. "
                    + "Supports page, size and sort query parameters.")
    @ApiResponse(responseCode = "200", description = "Page of tasks")
    public PagedModel<TaskResponse> getAll(
            @Parameter(description = "Filter by status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter by priority") @RequestParam(required = false) TaskPriority priority,
            Pageable pageable) {
        Page<TaskResponse> page = taskService.getAll(status, priority, pageable)
                .map(taskMapper::toResponse);
        return new PagedModel<>(page);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace a task", description = "Full update of a task's mutable fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Resource was modified by another request",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TaskResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateTaskRequest request) {
        var updated = taskService.update(id, taskMapper.toDto(request));
        return taskMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
