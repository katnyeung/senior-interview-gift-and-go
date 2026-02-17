package com.elemental.tech.workitems.api.dto;

import com.elemental.tech.workitems.domain.WorkItem;
import jakarta.validation.constraints.NotNull;

public record PatchWorkItemRequest(
        @NotNull(message ="status not null") WorkItem.Status status
) {}
