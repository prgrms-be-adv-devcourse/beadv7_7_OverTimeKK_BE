package com.programmers.kdt.user.repository;

import com.programmers.kdt.user.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
