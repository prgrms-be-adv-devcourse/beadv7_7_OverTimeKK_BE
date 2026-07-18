package com.programmers.kdt.venue.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hallId;

    @Column(nullable = false)
    private Long venueId;

    @Column(nullable = false)
    private String hallName;

    @Column(nullable = false)
    private Long seatTotalCount;
}
