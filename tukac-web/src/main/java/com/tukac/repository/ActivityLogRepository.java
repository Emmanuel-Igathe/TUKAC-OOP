package com.tukac.repository;

import com.tukac.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByOrderByTimestampDesc();
    List<ActivityLog> findByActionOrderByTimestampDesc(String action);
    List<ActivityLog> findByUserNameContainingIgnoreCaseOrderByTimestampDesc(String userName);
    long countByAction(String action);
}
