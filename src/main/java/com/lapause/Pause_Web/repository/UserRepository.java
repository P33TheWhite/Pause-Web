package com.lapause.Pause_Web.repository;

import com.lapause.Pause_Web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findByIsCotisationPendingTrue();

    List<User> findAllByOrderByPointsDesc();

    List<User> findAllByOrderByAllTimePointsDesc();
}
