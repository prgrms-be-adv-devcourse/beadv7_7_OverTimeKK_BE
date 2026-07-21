package com.programmers.kdt.venue.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hall")
public class Hall extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private Long hallId;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "hall_name", nullable = false)
    private String hallName;

    @Column(name = "seat_total_count", nullable = false)
    private Long seatTotalCount;

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();
}
