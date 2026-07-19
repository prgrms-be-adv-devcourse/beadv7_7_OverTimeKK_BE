package com.programmers.kdt.payment.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseTimeEntity {

    @Id
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private Long point;
}
