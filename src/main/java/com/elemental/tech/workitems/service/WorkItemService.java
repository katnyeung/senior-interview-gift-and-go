package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.exception.WorkItemNotFoundException;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkItemService {

    private final WorkItemRepository repository;

    public WorkItemService(WorkItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WorkItemResponse create(CreateWorkItemRequest request) {
        WorkItem workItem = new WorkItem(
                request.title(),
                request.description(),
                WorkItem.Priority.valueOf(request.priority().name())
        );

        WorkItem saved = repository.save(workItem);
        return WorkItemMapper.toResponse(saved);
    }

    @Transactional
    public WorkItemResponse transition(long id, PatchWorkItemRequest patch){
        WorkItem workItem = repository.findById(id)
                .orElseThrow(() -> new WorkItemNotFoundException("work item not found: " + id));

        workItem.transitionTo(patch.status());

        WorkItem workItemSaved = repository.save(workItem);

        return WorkItemMapper.toResponse(workItemSaved);

    }

    @Transactional(readOnly = true)
    public List<WorkItemResponse> list() {
        return repository.findAll()
                .stream()
                .map(WorkItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkItemResponse> listByStatus(WorkItem.Status status, Pageable pageable) {
        return repository.findByStatus(status, pageable)
                .stream()
                .map(WorkItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkItemResponse get(long id) {
        WorkItem workItem = repository.findById(id)
                .orElseThrow(() -> new WorkItemNotFoundException("work item not found: " + id));
        return WorkItemMapper.toResponse(workItem);
    }
}
