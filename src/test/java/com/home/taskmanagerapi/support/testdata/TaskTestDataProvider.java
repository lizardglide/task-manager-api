package com.home.taskmanagerapi.support.testdata;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.dto.request.CreateTaskRequest;
import com.home.taskmanagerapi.dto.request.UpdateTaskRequest;
import com.home.taskmanagerapi.dto.response.TaskResponse;
import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public final class TaskTestDataProvider {

    private TaskTestDataProvider() {
    }

    public static final UUID ID = UUID.fromString("d3b07384-d113-4f52-8703-9993339031c1");
    public static final String TITLE = "Write docs";
    public static final String DESCRIPTION = "Cover all endpoints and error codes";
    public static final TaskStatus STATUS_IN_PROGRESS = TaskStatus.IN_PROGRESS;
    public static final TaskStatus STATUS_DONE = TaskStatus.DONE;
    public static final TaskPriority PRIORITY_HIGH = TaskPriority.HIGH;
    public static final TaskPriority PRIORITY_LOW = TaskPriority.LOW;

    public static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    public static final Instant UPDATED_AT = Instant.parse("2026-01-02T10:00:00Z");

    public static final String NEW_TITLE = "Updated title";
    public static final String NEW_DESCRIPTION = "Updated description";

    public static final Pageable FIRST_PAGE = PageRequest.of(0, 10);

    public static Task.TaskBuilder taskBuilder() {
        return Task.builder()
                .id(ID).title(TITLE)
                .description(DESCRIPTION)
                .status(STATUS_IN_PROGRESS)
                .priority(PRIORITY_HIGH)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT);
    }

    public static Task task() {
        return taskBuilder().build();
    }

    public static TaskDto.TaskDtoBuilder taskDtoBuilder() {
        return TaskDto.builder()
                .id(ID).title(TITLE).description(DESCRIPTION)
                .status(STATUS_IN_PROGRESS).priority(PRIORITY_HIGH)
                .createdAt(CREATED_AT).updatedAt(UPDATED_AT);
    }

    public static TaskDto taskDto() {
        return taskDtoBuilder().build();
    }

    public static TaskDto createDto() {
        return TaskDto.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .priority(PRIORITY_HIGH)
                .build();
    }

    public static CreateTaskRequest.CreateTaskRequestBuilder createRequestBuilder() {
        return CreateTaskRequest.builder()
                .title(TITLE).description(DESCRIPTION).priority(PRIORITY_HIGH);
    }

    public static CreateTaskRequest createRequest() {
        return createRequestBuilder().build();
    }

    public static UpdateTaskRequest.UpdateTaskRequestBuilder updateRequestBuilder() {
        return UpdateTaskRequest.builder()
                .title(NEW_TITLE).description(NEW_DESCRIPTION)
                .status(STATUS_DONE).priority(PRIORITY_LOW);
    }

    public static UpdateTaskRequest updateRequest() {
        return updateRequestBuilder().build();
    }

    public static TaskResponse.TaskResponseBuilder taskResponseBuilder() {
        return TaskResponse.builder()
                .id(ID).title(TITLE).description(DESCRIPTION)
                .status(STATUS_IN_PROGRESS).priority(PRIORITY_HIGH)
                .createdAt(CREATED_AT).updatedAt(UPDATED_AT);
    }

}
