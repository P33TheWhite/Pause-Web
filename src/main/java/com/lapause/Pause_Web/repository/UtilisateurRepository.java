package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByEmail(String email);

    List<Utilisateur> findByDemandeCotisationEnCoursTrue();

    List<Utilisateur> findAllByOrderByPointsDesc();

    List<Utilisateur> findAllByOrderByPointsAllTimeDesc();
}