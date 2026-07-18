package com.programmers.kdt.performance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PerformanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionNum;

    @Column(nullable = false)
    private Long performanceInfoId;

    @Column(nullable = false)
    private String actor;


    @Column(nullable = false)
    private LocalDateTime performanceStartAt;
}
