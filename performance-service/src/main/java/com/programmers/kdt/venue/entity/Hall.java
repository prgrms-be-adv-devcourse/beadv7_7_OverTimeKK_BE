package com.programmers.kdt.venue.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Hall extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hallId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Venue venue;

    @NotNull
    private String hallName;

    @NotNull
    private Long seatTotalCount;

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();
}
