package com.tukac.repository;

import com.tukac.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
    List<EventRegistration> findByEventId(Long eventId);
    long countByEventId(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
