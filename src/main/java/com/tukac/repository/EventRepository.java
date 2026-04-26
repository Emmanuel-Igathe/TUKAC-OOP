package com.tukac.repository;

import com.tukac.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
    List<Event> findByDateBeforeOrderByDateDesc(LocalDate date);

    @Query("SELECT e FROM Event e ORDER BY e.date ASC")
    List<Event> findAllOrderByDate();
}
