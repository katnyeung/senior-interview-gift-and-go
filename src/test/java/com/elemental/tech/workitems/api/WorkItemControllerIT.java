package com.elemental.tech.workitems.api;

// TODO: 2. Left imports as a hint.
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.persistence.RequestHistoryRepository;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class WorkItemControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WorkItemRepository workItemRepository;

    @Autowired
    RequestHistoryRepository requestHistoryRepository;

    @Test
    void createNewWorkItem() throws Exception {
        var req = new CreateWorkItemRequest("Hello", "World", CreateWorkItemRequest.Priority.MEDIUM);

        // TODO: 1. Please identify an appropriate testing strat
        String response = mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        WorkItemResponse workItemResponse = objectMapper.readValue(response, WorkItemResponse.class);
        assertThat(workItemResponse.title()).isEqualTo("Hello");
        assertThat(workItemResponse.status()).isEqualTo("OPEN");
    }

    // TODO: 3. enhance and add further tests
    @Test
    void shouldSuccessfulTransitStatus() throws Exception {
        var req = new CreateWorkItemRequest("Hello", "World", CreateWorkItemRequest.Priority.MEDIUM);

        // TODO: 1. Please identify an appropriate testing strat
        String response = mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        WorkItemResponse workItemResponse = objectMapper.readValue(response, WorkItemResponse.class);

        mvc.perform(patch("/api/work-items/" + workItemResponse.id() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PatchWorkItemRequest(WorkItem.Status.IN_PROGRESS))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowWithInvalidTransitStatus() throws Exception {
        var req = new CreateWorkItemRequest("Hello", "World", CreateWorkItemRequest.Priority.MEDIUM);

        // TODO: 1. Please identify an appropriate testing strat
        String response = mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        WorkItemResponse workItemResponse = objectMapper.readValue(response, WorkItemResponse.class);

        mvc.perform(patch("/api/work-items/" + workItemResponse.id() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PatchWorkItemRequest(WorkItem.Status.DONE))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateWorkItemAndRecordHistory() throws Exception {
        var req = new CreateWorkItemRequest("Task", "Desc", CreateWorkItemRequest.Priority.HIGH);

        mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // verify work item saved
        assertThat(workItemRepository.findAll()).hasSizeGreaterThan(1);

        // verify audit log
        assertThat(requestHistoryRepository.findAll()).hasSizeGreaterThan(1);
    }

    @Test
    void shouldReturn400() throws Exception {
        var req = new CreateWorkItemRequest("", "", CreateWorkItemRequest.Priority.MEDIUM);

        mvc.perform(post("/api/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturn400WithEmptyRequest() throws Exception {
        mvc.perform(post("/api/work-items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reqShouldReturnOK() throws Exception {
        mvc.perform(get("/api/work-items"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WithNotExistId() throws Exception {
        mvc.perform(get("/api/work-items/123"))
                .andExpect(status().isNotFound());
    }
}
