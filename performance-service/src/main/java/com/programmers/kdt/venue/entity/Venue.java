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
public class Venue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    @NotNull
    private String venueName;

    @NotNull
    private String roadAddress;

    @NotNull
    private String detailAddress;

    @NotNull
    private String notice;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Hall> halls = new ArrayList<>();
}
