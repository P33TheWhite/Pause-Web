package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findByEventIdAndIsWaitingTrueOrderByRegistrationDateAsc(Long eventId);

    long countByEventIdAndIsWaitingFalse(Long eventId);

    Registration findByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findByUserId(Long userId);
}
