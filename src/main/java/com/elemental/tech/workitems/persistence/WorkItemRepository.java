package com.elemental.tech.workitems.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.elemental.tech.workitems.domain.WorkItem;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
    // TODO: add query methods for filtering & sorting & pagination.
    Page<WorkItem> findByStatus(WorkItem.Status status, Pageable pageable);

}
