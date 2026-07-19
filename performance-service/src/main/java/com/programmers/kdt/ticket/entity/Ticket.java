package com.programmers.kdt.ticket.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
public class Ticket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @NotNull
    private Long performanceInfoId;

    @NotNull
    private Long sessionNum;

    @NotNull
    @Column(nullable = false)
    private String zone;

    @NotNull
    @Column(nullable = false)
    private String seatRow;

    @NotNull
    @Column(nullable = false)
    private String seatNum;

    @NotNull
    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'AVAILABLE'")
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;
}
