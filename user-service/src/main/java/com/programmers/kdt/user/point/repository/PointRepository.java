package com.programmers.kdt.user.point.repository;

import com.programmers.kdt.user.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
