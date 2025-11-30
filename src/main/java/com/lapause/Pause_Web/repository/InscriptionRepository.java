package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEvenementId(Long evenementId);

    boolean existsByUtilisateurIdAndEvenementId(Long userId, Long eventId);

    List<Inscription> findByEvenementIdAndEnAttenteTrueOrderByDateInscriptionAsc(Long evenementId);

    long countByEvenementIdAndEnAttenteFalse(Long evenementId);

    Inscription findByUtilisateurIdAndEvenementId(Long userId, Long eventId);

    List<Inscription> findByUtilisateurId(Long userId);
}