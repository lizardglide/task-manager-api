package com.home.taskmanagerapi.service;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import com.home.taskmanagerapi.exception.custom.TaskNotFoundException;
import com.home.taskmanagerapi.repository.TaskRepository;
import com.home.taskmanagerapi.support.integrationtest.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.home.taskmanagerapi.support.testdata.TaskTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Service integration test against a real Postgres.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TaskServiceIT extends AbstractPostgresIntegrationTest {

    @Autowired
    TaskService service;

    @Autowired
    TaskRepository taskRepository;

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    private static TaskDto dto(TaskStatus status, TaskPriority priority) {
        return TaskDto.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .status(status)
                .priority(priority)
                .build();
    }

    @Test
    void create_persistsWithGeneratedIdOpenStatusAndTimestamps() {
        var created = service.create(createDto());

        assertAll(
                () -> assertNotNull(created.id()),
                () -> assertEquals(TaskStatus.OPEN, created.status()),
                () -> assertEquals(TITLE, created.title())
        );

        var fetched = service.getById(created.id());
        assertAll(
                () -> assertNotNull(fetched.createdAt()),
                () -> assertNotNull(fetched.updatedAt())
        );
    }

    @Test
    void getById_throwsNotFound_whenAbsent() {
        assertThrows(TaskNotFoundException.class, () -> service.getById(UUID.randomUUID()));
    }

    @Test
    void update_replacesMutableFields() {
        var id = service.create(createDto()).id();

        var changes = TaskDto.builder()
                .title(NEW_TITLE)
                .description(NEW_DESCRIPTION)
                .status(STATUS_DONE)
                .priority(PRIORITY_LOW)
                .build();
        var updated = service.update(id, changes);

        assertAll(
                () -> assertEquals(id, updated.id()),
                () -> assertEquals(NEW_TITLE, updated.title()),
                () -> assertEquals(STATUS_DONE, updated.status()),
                () -> assertEquals(PRIORITY_LOW, updated.priority())
        );
    }

    @Test
    void update_throwsNotFound_whenAbsent() {
        assertThrows(TaskNotFoundException.class,
                () -> service.update(UUID.randomUUID(), createDto()));
    }

    @Test
    void delete_removesTask() {
        var id = service.create(createDto()).id();

        service.delete(id);

        assertThrows(TaskNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void delete_throwsNotFound_whenAbsent() {
        assertThrows(TaskNotFoundException.class, () -> service.delete(UUID.randomUUID()));
    }

    @Test
    void getAll_filtersByStatusAndPriority() {
        service.create(dto(TaskStatus.OPEN, TaskPriority.HIGH));
        service.create(dto(TaskStatus.OPEN, TaskPriority.LOW));
        service.create(dto(TaskStatus.DONE, TaskPriority.HIGH));

        assertAll(
                () -> assertEquals(3, service.getAll(null, null, FIRST_PAGE).getTotalElements()),
                () -> assertEquals(2, service.getAll(TaskStatus.OPEN, null, FIRST_PAGE).getTotalElements()),
                () -> assertEquals(2, service.getAll(null, TaskPriority.HIGH, FIRST_PAGE).getTotalElements()),
                () -> assertEquals(1, service.getAll(TaskStatus.OPEN, TaskPriority.HIGH, FIRST_PAGE).getTotalElements())
        );
    }
}

