package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    //
}