package com.elemental.tech.workitems.domain;

import com.elemental.tech.workitems.exception.InvalidWorkItemStateException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WorkItemTest {

    @Test
    public void shouldCreateWithOpenStatus(){
        WorkItem workItem = new WorkItem("title", "description", WorkItem.Priority.HIGH);

        assertThat(workItem.getTitle()).isEqualTo("title");
        assertThat(workItem.getDescription()).isEqualTo("description");
        assertThat(workItem.getPriority()).isEqualTo(WorkItem.Priority.HIGH);
        assertThat(workItem.getStatus()).isEqualTo(WorkItem.Status.OPEN);
        assertThat(workItem.getCreatedAt()).isNotNull();
        assertThat(workItem.getUpdatedAt()).isEqualTo(workItem.getCreatedAt());
    }

    @Test
    public void shouldProperlyUpdated(){

        WorkItem workItem = new WorkItem("title", "description", WorkItem.Priority.HIGH);
        workItem.setTitle("new title");
        workItem.setDescription("new description");

        Instant lastUpdate = workItem.getUpdatedAt();

        assertThat(workItem.getTitle()).isEqualTo("new title");
        assertThat(workItem.getDescription()).isEqualTo("new description");
        assertThat(lastUpdate).isAfterOrEqualTo(lastUpdate);
    }

    @Test
    public void shouldThrowWithInvalidTransition(){

        WorkItem workItem = new WorkItem("title", "description", WorkItem.Priority.HIGH);

        assertThatThrownBy(() -> workItem.transitionTo(WorkItem.Status.DONE))
                .isInstanceOf(InvalidWorkItemStateException.class);
    }

    @Test
    public void shouldValidStatusTranition(){

        WorkItem workItem = new WorkItem("title", "description", WorkItem.Priority.HIGH);
        workItem.transitionTo(WorkItem.Status.IN_PROGRESS);

        assertThat(workItem.getStatus()).isEqualTo(WorkItem.Status.IN_PROGRESS);
    }
}
