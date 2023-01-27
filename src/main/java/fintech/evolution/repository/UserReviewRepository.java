package fintech.evolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fintech.evolution.variable.entity.UserReview;

import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    Optional<UserReview> findByPhoneNumber(String phoneNumber);
}