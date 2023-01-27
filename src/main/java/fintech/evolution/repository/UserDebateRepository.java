package fintech.evolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fintech.evolution.variable.entity.user.UserDebate;

import java.util.Optional;

public interface UserDebateRepository extends JpaRepository<UserDebate, Long> {

    Optional<UserDebate> findByStir(Long stir);
}