package com.elemental.tech.workitems.persistence;

import com.elemental.tech.workitems.domain.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

}
