package com.home.taskmanagerapi.repository;

import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import com.home.taskmanagerapi.util.specification.TaskSpecification;
import com.home.taskmanagerapi.support.integrationtest.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;

import static com.home.taskmanagerapi.support.testdata.TaskTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Persistence-layer integration test against real PostgreSQL.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIT extends AbstractPostgresIntegrationTest {

    private static final String CHANGED_TITLE = "changed title";

    @Autowired
    TaskRepository taskRepository;

    private static Task task(TaskStatus status, TaskPriority priority) {
        return Task.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .status(status)
                .priority(priority)
                .build();
    }

    @Test
    void persists_andGeneratesIdAndTimestamps() {
        Task saved = taskRepository.saveAndFlush(task(TaskStatus.OPEN, TaskPriority.HIGH));

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertNotNull(saved.getCreatedAt()),
                () -> assertNotNull(saved.getUpdatedAt())
        );
    }

    @Test
    void filtersByStatus() {
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.LOW));
        taskRepository.save(task(TaskStatus.DONE, TaskPriority.LOW));

        Page<Task> result = taskRepository.findAll(
                TaskSpecification.withFilters(TaskStatus.OPEN, null), FIRST_PAGE);

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(TaskStatus.OPEN, result.getContent().get(0).getStatus())
        );
    }

    @Test
    void filtersByPriority() {
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.HIGH));
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.LOW));

        Page<Task> result = taskRepository.findAll(
                TaskSpecification.withFilters(null, TaskPriority.HIGH), FIRST_PAGE);

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(TaskPriority.HIGH, result.getContent().get(0).getPriority())
        );
    }

    @Test
    void filtersByStatusAndPriority() {
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.HIGH));
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.LOW));
        taskRepository.save(task(TaskStatus.DONE, TaskPriority.HIGH));

        Page<Task> result = taskRepository.findAll(
                TaskSpecification.withFilters(TaskStatus.OPEN, TaskPriority.HIGH), FIRST_PAGE);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void noFilters_returnsAll() {
        taskRepository.save(task(TaskStatus.OPEN, TaskPriority.HIGH));
        taskRepository.save(task(TaskStatus.DONE, TaskPriority.LOW));

        Page<Task> result = taskRepository.findAll(
                TaskSpecification.withFilters(null, null), FIRST_PAGE);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void version_incrementsOnUpdate() {
        Task saved = taskRepository.saveAndFlush(task(TaskStatus.OPEN, TaskPriority.LOW));
        assertEquals(0L, saved.getVersion());

        saved.setTitle(CHANGED_TITLE);
        Task updated = taskRepository.saveAndFlush(saved);

        assertEquals(1L, updated.getVersion());
    }

}
