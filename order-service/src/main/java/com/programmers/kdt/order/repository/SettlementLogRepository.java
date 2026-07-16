package com.programmers.kdt.order.repository;

import com.programmers.kdt.order.entity.SettlementLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementLogRepository extends JpaRepository<SettlementLog, Long> {
}
