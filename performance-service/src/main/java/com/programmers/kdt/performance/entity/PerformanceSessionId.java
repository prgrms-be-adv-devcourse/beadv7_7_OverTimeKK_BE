package com.programmers.kdt.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PerformanceSessionId implements Serializable {

    @NotNull
    @Column(nullable = false)
    private Long sessionNum;

    @NotNull
    @Column(nullable = false)
    private Long performanceInfoId;
}
