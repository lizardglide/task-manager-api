package com.home.taskmanagerapi.controller;

import com.home.taskmanagerapi.dto.TaskDto;
import com.home.taskmanagerapi.exception.custom.TaskNotFoundException;
import com.home.taskmanagerapi.mapper.TaskMapperImpl;
import com.home.taskmanagerapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.home.taskmanagerapi.support.testdata.TaskTestDataProvider.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer tests for {@link TaskController}.
 */
@WebMvcTest(TaskController.class)
@Import(TaskMapperImpl.class)
class TaskControllerTest {

    private static final String BASE = "/api/v1/tasks";
    private static final String INVALID_REQUEST = "Invalid request";
    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ID_PATH = "/{id}";
    private static final String LOCATION = "Location";
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;

    @TestConfiguration
    @EnableSpringDataWebSupport
    static class DataWebConfig {
    }

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaskService taskService;

    @Test
    void create_returns201AndBody_whenValid() throws Exception {
        when(taskService.create(any(TaskDto.class))).thenReturn(taskDto());

        var body = """
                {"title":"Write integration tests",
                "description":"cover the web layer",
                "priority":"HIGH"}
                """;

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, endsWith(BASE + "/" + ID)))
                .andExpect(jsonPath("$.id", is(ID.toString())))
                .andExpect(jsonPath("$.title", is(TITLE)))
                .andExpect(jsonPath("$.status", is(STATUS_IN_PROGRESS.name())));
    }

    @Test
    void create_returns400_whenTitleTooShort() throws Exception {
        var body = """
                {"title":"ab",
                "priority":"HIGH"}
                """;

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(STATUS_CODE_400)))
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.title").doesNotExist());
    }

    @Test
    void create_returns400_whenTitleBlank() throws Exception {
        var body = """
                {"title":"   ",
                "priority":"HIGH"}
                """;

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void create_returns400_whenPriorityMissing() throws Exception {
        var body = """
                {"title":"Valid title"}
                """;

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void create_returns400_whenPriorityIsInvalidEnum() throws Exception {
        var body = """
                {"title":"Valid title",
                "priority":"URGENT"}
                """;

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void create_returns400_whenBodyMalformed() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void getById_returns200_whenFound() throws Exception {
        when(taskService.getById(ID)).thenReturn(taskDto());

        mockMvc.perform(get(BASE + ID_PATH, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID.toString())));
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        when(taskService.getById(ID)).thenThrow(new TaskNotFoundException(ID));

        mockMvc.perform(get(BASE + ID_PATH, ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(STATUS_CODE_404)))
                .andExpect(jsonPath("$.error", is(RESOURCE_NOT_FOUND))); // generic; the id never leaks
    }

    @Test
    void getById_returns400_whenIdNotUuid() throws Exception {
        mockMvc.perform(get(BASE + ID_PATH, "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void getAll_returns200_withPagedBody() throws Exception {
        Page<TaskDto> page = new PageImpl<>(List.of(taskDto()));
        when(taskService.getAll(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(ID.toString())));
    }

    @Test
    void getAll_returns400_whenStatusFilterInvalid() throws Exception {
        mockMvc.perform(get(BASE).param("status", "NOPE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        when(taskService.update(eq(ID), any(TaskDto.class))).thenReturn(taskDto());

        var body = """
                {"title":"Updated title",
                "description":"d",
                "status":"DONE",
                "priority":"LOW"}
                """;

        mockMvc.perform(put(BASE + ID_PATH, ID).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID.toString())));
    }

    @Test
    void update_returns400_whenStatusMissing() throws Exception {
        var body = """
                {"title":"Updated title",
                "priority":"LOW"}
                """;

        mockMvc.perform(put(BASE + ID_PATH, ID).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(INVALID_REQUEST)));
    }

    @Test
    void update_returns404_whenMissing() throws Exception {
        when(taskService.update(eq(ID), any(TaskDto.class))).thenThrow(new TaskNotFoundException(ID));

        var body = """
                {"title":"Updated title",
                "status":"DONE",
                "priority":"LOW"}
                """;

        mockMvc.perform(put(BASE + ID_PATH, ID).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(RESOURCE_NOT_FOUND)));
    }

    @Test
    void delete_returns204_whenFound() throws Exception {
        mockMvc.perform(delete(BASE + ID_PATH, ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404_whenMissing() throws Exception {
        doThrow(new TaskNotFoundException(ID)).when(taskService).delete(ID);

        mockMvc.perform(delete(BASE + ID_PATH, ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(RESOURCE_NOT_FOUND)));
    }
}
