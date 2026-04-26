package com.tukac.repository;

import com.tukac.model.EventRegistration;
import com.tukac.model.Event;
import com.tukac.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventAndUser(Event event, User user);
    Optional<EventRegistration> findByEventAndUser(Event event, User user);
    List<EventRegistration> findByUser(User user);
    long countByEvent(Event event);
}
