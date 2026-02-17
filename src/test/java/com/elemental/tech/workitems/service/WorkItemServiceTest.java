package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.PatchWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.exception.InvalidWorkItemStateException;
import com.elemental.tech.workitems.exception.WorkItemNotFoundException;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WorkItemServiceTest {

    @Mock
    WorkItemRepository workItemRepository;

    @InjectMocks
    WorkItemService workItemService;

    @Test
    public void shouldCreateItem(){
        //given
        CreateWorkItemRequest workRequest = new CreateWorkItemRequest("title", "description", CreateWorkItemRequest.Priority.LOW);
        WorkItem workItem = new WorkItem("title", "description", WorkItem.Priority.LOW);
        ReflectionTestUtils.setField(workItem, "id", 1L);

        //when
        when(workItemRepository.save(any(WorkItem.class))).thenReturn(workItem);
        WorkItemResponse response = workItemService.create(workRequest);

        //then
        assertThat(response.title()).isEqualTo("title");
        verify(workItemRepository).save(any(WorkItem.class));

    }

    @Test
    void shouldReturnWorkItemById() {
        // given
        WorkItem workItem = new WorkItem("task", "desc", WorkItem.Priority.HIGH);
        ReflectionTestUtils.setField(workItem, "id", 1L);
        when(workItemRepository.findById(1L)).thenReturn(Optional.of(workItem));

        // when
        WorkItemResponse response = workItemService.get(1L);

        // then
        assertThat(response.title()).isEqualTo("task");
        assertThat(response.priority()).isEqualTo("HIGH");
        verify(workItemRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenWorkItemNotFound() {
        // given
        when(workItemRepository.findById(999L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> workItemService.get(999L))
                .isInstanceOf(WorkItemNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldTransitionStatus() {
        // given
        WorkItem workItem = new WorkItem("task", "desc", WorkItem.Priority.MEDIUM);
        ReflectionTestUtils.setField(workItem, "id", 1L);
        when(workItemRepository.findById(1L)).thenReturn(Optional.of(workItem));
        when(workItemRepository.save(any(WorkItem.class))).thenReturn(workItem);

        // when
        WorkItemResponse response = workItemService.transition(1L, new PatchWorkItemRequest(WorkItem.Status.IN_PROGRESS));

        // then
        assertThat(response.status()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void shouldThrowOnInvalidTransition() {
        // given
        WorkItem workItem = new WorkItem("task", "desc", WorkItem.Priority.MEDIUM);
        ReflectionTestUtils.setField(workItem, "id", 1L);
        when(workItemRepository.findById(1L)).thenReturn(Optional.of(workItem));

        // then
        assertThatThrownBy(() -> workItemService.transition(1L, new PatchWorkItemRequest(WorkItem.Status.DONE)))
                .isInstanceOf(InvalidWorkItemStateException.class);
    }

}
