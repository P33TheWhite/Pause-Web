package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    //
}
