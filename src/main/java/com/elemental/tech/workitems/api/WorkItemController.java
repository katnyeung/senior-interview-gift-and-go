package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.service.WorkItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    TODO: please enhance
 */
@RestController
@RequestMapping("/api/work-items")
@Slf4j
public class WorkItemController {

    private final WorkItemService service;

    public WorkItemController(WorkItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkItemResponse create (@Valid @RequestBody CreateWorkItemRequest request) {
        log.info("Creating work item, title : {}", request.title());
        return service.create(request);
    }

    @GetMapping
    public List<WorkItemResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public WorkItemResponse get(@PathVariable long id) {
        return service.get(id);
    }

    // TODO: implement PATCH endpoint for status transition with validation & concur strategy.
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public WorkItemResponse transition (@PathVariable long id, @RequestBody PatchWorkItemRequest request) {
        log.info("Transitioning work item {} to status {}", id, request.status());
        return service.transition(id, request);
    }
}
