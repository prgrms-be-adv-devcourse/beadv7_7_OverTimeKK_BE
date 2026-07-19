package com.programmers.kdt.payment.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "use_type", nullable = false)
    private PointType useType;

    @Column(name = "amount", nullable = false)
    private Long amount;
}
