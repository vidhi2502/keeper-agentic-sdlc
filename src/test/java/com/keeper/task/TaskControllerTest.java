package com.keeper.task;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        when(taskService.listTasks()).thenReturn(List.of(
                new Task(1, "first", false),
                new Task(2, "second", true)));
    }

    @Test
    void listsTasksInServiceOrder() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("first"))
                .andExpect(jsonPath("$[1].title").value("second"));
    }

    @Test
    void createsTaskAndReturnsCreatedStatus() throws Exception {
        when(taskService.createTask("Write tests")).thenReturn(new Task(3, "Write tests", false));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write tests\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void returnsBadRequestForInvalidTitle() throws Exception {
        when(taskService.createTask(" ")).thenThrow(new InvalidTaskTitleException("Title must not be empty."));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_TITLE"))
                .andExpect(jsonPath("$.error.message").value("Title must not be empty."));
    }

    @Test
    void updatesCompletionState() throws Exception {
        when(taskService.setDone(1, true)).thenReturn(new Task(1, "first", true));

        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void returnsNotFoundForUnknownTask() throws Exception {
        when(taskService.setDone(999, true)).thenThrow(new TaskNotFoundException(999));

        mockMvc.perform(patch("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("Task 999 was not found."));
    }

    @Test
    void rejectsMissingCompletionValue() throws Exception {
        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void deletesTaskWithNoContentResponse() throws Exception {
        doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsNotFoundWhenDeletingUnknownTask() throws Exception {
        doThrow(new TaskNotFoundException(999)).when(taskService).deleteTask(999);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TASK_NOT_FOUND"));
    }

    @Test
    void rejectsMalformedJsonWithStructuredError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void returnsThousandTasksWithinHalfSecond() throws Exception {
        List<Task> manyTasks = new ArrayList<>();
        for (long id = 1; id <= 1000; id++) {
            manyTasks.add(new Task(id, "task-" + id, false));
        }
        when(taskService.listTasks()).thenReturn(manyTasks);
        mockMvc.perform(get("/api/tasks")).andExpect(status().isOk());

        long start = System.nanoTime();
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1000)));
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        org.junit.jupiter.api.Assertions.assertTrue(elapsedMillis < 500,
                "Expected 1,000-task API response under 500 ms, actual: " + elapsedMillis + " ms");
    }
}
