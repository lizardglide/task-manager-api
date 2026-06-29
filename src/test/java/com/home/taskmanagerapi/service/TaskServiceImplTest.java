package com.home.taskmanagerapi.service;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.entity.Task;
import com.home.taskmanagerapi.enums.TaskStatus;
import com.home.taskmanagerapi.exception.custom.TaskNotFoundException;
import com.home.taskmanagerapi.mapper.TaskMapper;
import com.home.taskmanagerapi.repository.TaskRepository;
import com.home.taskmanagerapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.home.taskmanagerapi.support.testdata.TaskTestDataProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    TaskServiceImpl service;

    @Test
    void create_defaultsStatusToOpen_whenNotProvided() {
        var input = taskDtoBuilder().status(null).build();
        var fromMapper = taskBuilder().status(null).build();
        when(taskMapper.toEntity(input)).thenReturn(fromMapper);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto());

        service.create(input);

        ArgumentCaptor<Task> saved = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(saved.capture());
        assertEquals(TaskStatus.OPEN, saved.getValue().getStatus());
    }

    @Test
    void create_keepsProvidedStatus() {
        var input = taskDtoBuilder().build(); // status = STATUS
        when(taskMapper.toEntity(input)).thenReturn(task());
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto());

        service.create(input);

        ArgumentCaptor<Task> saved = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(saved.capture());
        assertEquals(STATUS_IN_PROGRESS, saved.getValue().getStatus());
    }

    @Test
    void getById_returnsDto_whenFound() {
        var entity = task();
        when(taskRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(taskMapper.toDto(entity)).thenReturn(taskDto());

        assertEquals(ID, service.getById(ID).id());
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(taskRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.getById(ID));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_queriesWithSpecificationAndPageable() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Task> page = new PageImpl<>(List.of(task()));
        when(taskRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto());

        Page<TaskDto> result = service.getAll(STATUS_IN_PROGRESS, PRIORITY_HIGH, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void update_appliesChangesAndSaves_whenFound() {
        Task existing = task();
        when(taskRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);
        when(taskMapper.toDto(existing)).thenReturn(taskDto());

        service.update(ID, taskDtoBuilder().build());

        verify(taskMapper).updateEntityFromDto(any(TaskDto.class), eq(existing));
        verify(taskRepository).save(existing);
    }

    @Test
    void update_throwsNotFound_whenMissing() {
        when(taskRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.update(ID, taskDto()));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void delete_removes_whenExists() {
        var existing = task();
        when(taskRepository.findById(ID)).thenReturn(Optional.of(existing));

        service.delete(ID);

        verify(taskRepository).delete(existing);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(taskRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.delete(ID));
        verify(taskRepository, never()).delete((Task) any());

    }
}
