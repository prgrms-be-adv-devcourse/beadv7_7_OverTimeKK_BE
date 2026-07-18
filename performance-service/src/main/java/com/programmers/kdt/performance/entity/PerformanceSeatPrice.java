package com.programmers.kdt.performance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PerformanceSeatPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long performanceInfoId;

    @Column(nullable = false)
    private Long hallId;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private Long price;
}
