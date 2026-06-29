package com.home.taskmanagerapi.service.impl;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.enums.TaskPriority;
import com.home.taskmanagerapi.enums.TaskStatus;
import com.home.taskmanagerapi.exception.custom.TaskNotFoundException;
import com.home.taskmanagerapi.mapper.TaskMapper;
import com.home.taskmanagerapi.repository.TaskRepository;
import com.home.taskmanagerapi.service.TaskService;
import com.home.taskmanagerapi.util.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskDto create(TaskDto task) {
        var entity = taskMapper.toEntity(task);
        if (entity.getStatus() == null) {
            entity.setStatus(TaskStatus.OPEN);
        }
        var saved = taskRepository.save(entity);
        return taskMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getById(UUID id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getAll(TaskStatus status, TaskPriority priority, Pageable pageable) {
        Specification<Task> spec = TaskSpecification.withFilters(status, priority);
        return taskRepository.findAll(spec, pageable).map(taskMapper::toDto);
    }

    @Override
    @Transactional
    public TaskDto update(UUID id, TaskDto task) {
        var existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateEntityFromDto(task, existing);
        return taskMapper.toDto(taskRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        var existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(existing);
    }

}
