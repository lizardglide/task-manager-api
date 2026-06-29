package com.home.taskmanagerapi.util.specification;

import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.entity.Task_;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Composable, optional filters for Task queries.
 */
public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> withFilters(TaskStatus status, TaskPriority priority) {
        return Specification.allOf(
                hasStatus(status),
                hasPriority(priority)
        );
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get(Task_.STATUS), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                priority == null ? null : cb.equal(root.get(Task_.PRIORITY), priority);
    }

}
