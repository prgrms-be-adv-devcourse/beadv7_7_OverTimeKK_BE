package com.programmers.kdt.performance.repository;

import com.programmers.kdt.performance.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
