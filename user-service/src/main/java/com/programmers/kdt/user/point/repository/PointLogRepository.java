package com.programmers.kdt.user.point.repository;

import com.programmers.kdt.user.point.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    List<PointLog> findByUserId(Long userId);
}
