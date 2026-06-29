package com.home.taskmanagerapi.service;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {

    TaskDto create(TaskDto task);

    TaskDto getById(UUID id);

    Page<TaskDto> getAll(TaskStatus status, TaskPriority priority, Pageable pageable);

    TaskDto update(UUID id, TaskDto task);

    void delete(UUID id);
}
