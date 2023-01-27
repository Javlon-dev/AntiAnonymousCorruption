package fintech.evolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fintech.evolution.variable.entity.user.SecondUserDebate;

import java.util.Optional;

public interface SecondUserDebateRepository extends JpaRepository<SecondUserDebate, Long> {
    Optional<SecondUserDebate> findByStir(Long stir);
}