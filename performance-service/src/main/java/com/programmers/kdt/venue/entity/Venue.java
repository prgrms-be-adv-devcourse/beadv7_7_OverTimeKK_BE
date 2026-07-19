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
public class Venue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    @Column(nullable = false)
    private String venueName;

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private String notice;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Hall> halls = new ArrayList<>();
}
