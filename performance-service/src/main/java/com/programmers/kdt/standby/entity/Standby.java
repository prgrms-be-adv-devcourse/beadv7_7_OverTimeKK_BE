package com.programmers.kdt.standby.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Standby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long standbyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long performanceInfoId;

    @Column(nullable = false)
    private Long sessionNum;

    private String zone1;
    private String zone2;
    private String zone3;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'WAITING'")
    private StandbyStatus standbyStatus = StandbyStatus.WAITING;

    @Column(nullable = false)
    private LocalDateTime reservedAt;
}
