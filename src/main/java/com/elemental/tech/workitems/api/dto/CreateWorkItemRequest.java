package com.elemental.tech.workitems.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
    TODO: please enhance with validation
 */
public record CreateWorkItemRequest(
        @NotBlank(message ="title not blank") String title,
        @NotBlank(message ="description not blank") String description,
        @NotNull(message ="priority not null") Priority priority
) {
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
