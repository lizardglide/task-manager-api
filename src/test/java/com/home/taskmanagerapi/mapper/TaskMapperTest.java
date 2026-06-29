package com.home.taskmanagerapi.mapper;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.dto.request.CreateTaskRequest;
import com.home.taskmanagerapi.dto.request.UpdateTaskRequest;
import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.home.taskmanagerapi.support.testdata.TaskTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private final TaskMapper mapper = new TaskMapperImpl();

    @Test
    void toDto_fromCreateRequest_mapsClientFields_andLeavesServerFieldsNull() {
        var dto = mapper.toDto(createRequest());

        assertAll(
                () -> assertEquals(TITLE, dto.title()),
                () -> assertEquals(DESCRIPTION, dto.description()),
                () -> assertEquals(PRIORITY_HIGH, dto.priority()),
                () -> assertNull(dto.id()),
                () -> assertNull(dto.status()),
                () -> assertNull(dto.createdAt()),
                () -> assertNull(dto.updatedAt())
        );
    }

    @Test
    void toDto_fromUpdateRequest_mapsFieldsIncludingStatus() {
        var dto = mapper.toDto(updateRequest());

        assertAll(
                () -> assertEquals(NEW_TITLE, dto.title()),
                () -> assertEquals(NEW_DESCRIPTION, dto.description()),
                () -> assertEquals(STATUS_DONE, dto.status()),
                () -> assertEquals(PRIORITY_LOW, dto.priority()),
                () -> assertNull(dto.id()),
                () -> assertNull(dto.createdAt()),
                () -> assertNull(dto.updatedAt())
        );
    }

    @Test
    void toEntity_mapsBusinessFields_andIgnoresHibernateOwnedFields() {
        var entity = mapper.toEntity(taskDto());

        assertAll(
                () -> assertEquals(TITLE, entity.getTitle()),
                () -> assertEquals(DESCRIPTION, entity.getDescription()),
                () -> assertEquals(STATUS_IN_PROGRESS, entity.getStatus()),
                () -> assertEquals(PRIORITY_HIGH, entity.getPriority()),
                () -> assertNull(entity.getId()),
                () -> assertNull(entity.getCreatedAt()),
                () -> assertNull(entity.getUpdatedAt())
        );
    }

    @Test
    void toDto_fromEntity_mapsAllFields() {
        var dto = mapper.toDto(task());

        assertAll(
                () -> assertEquals(ID, dto.id()),
                () -> assertEquals(TITLE, dto.title()),
                () -> assertEquals(DESCRIPTION, dto.description()),
                () -> assertEquals(STATUS_IN_PROGRESS, dto.status()),
                () -> assertEquals(PRIORITY_HIGH, dto.priority()),
                () -> assertEquals(CREATED_AT, dto.createdAt()),
                () -> assertEquals(UPDATED_AT, dto.updatedAt())
        );
    }

    @Test
    void updateEntityFromDto_updatesMutableFields_andPreservesIdAndCreatedAt() {
        var entity = task();
        var changes = TaskDto.builder()
                .title(NEW_TITLE)
                .description(NEW_DESCRIPTION)
                .status(STATUS_DONE)
                .priority(PRIORITY_LOW)
                .build();

        mapper.updateEntityFromDto(changes, entity);

        assertAll(
                () -> assertEquals(NEW_TITLE, entity.getTitle()),
                () -> assertEquals(NEW_DESCRIPTION, entity.getDescription()),
                () -> assertEquals(STATUS_DONE, entity.getStatus()),
                () -> assertEquals(PRIORITY_LOW, entity.getPriority()),
                () -> assertEquals(ID, entity.getId()),
                () -> assertEquals(CREATED_AT, entity.getCreatedAt())
        );
    }

    @Test
    void toResponse_mapsAllFields() {
        var response = mapper.toResponse(taskDto());

        assertAll(
                () -> assertEquals(ID, response.id()),
                () -> assertEquals(TITLE, response.title()),
                () -> assertEquals(DESCRIPTION, response.description()),
                () -> assertEquals(STATUS_IN_PROGRESS, response.status()),
                () -> assertEquals(PRIORITY_HIGH, response.priority()),
                () -> assertEquals(CREATED_AT, response.createdAt()),
                () -> assertEquals(UPDATED_AT, response.updatedAt())
        );
    }

    @Test
    void nullInputs_returnNull() {
        assertAll(
                () -> assertNull(mapper.toDto((CreateTaskRequest) null)),
                () -> assertNull(mapper.toDto((UpdateTaskRequest) null)),
                () -> assertNull(mapper.toDto((Task) null)),
                () -> assertNull(mapper.toEntity(null)),
                () -> assertNull(mapper.toResponse(null))
        );
    }

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    void toDto_fromEntity_preservesEveryStatus(TaskStatus status) {
        var entity = taskBuilder().status(status).build();

        assertEquals(status, mapper.toDto(entity).status());
    }

    @ParameterizedTest
    @EnumSource(TaskPriority.class)
    void toResponse_preservesEveryPriority(TaskPriority priority) {
        var dto = taskDtoBuilder().priority(priority).build();

        assertEquals(priority, mapper.toResponse(dto).priority());
    }
}
