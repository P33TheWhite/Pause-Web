package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    //
}
