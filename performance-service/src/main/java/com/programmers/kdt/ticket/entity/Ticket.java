package com.programmers.kdt.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false)
    private Long performanceInfoId;

    @Column(nullable = false)
    private Long sessionNum;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private String seatRow;

    @Column(nullable = false)
    private String seatNum;

    @Column(nullable = false)
    private Long price;

    // ERD: 상태 Enum NOT NULL DEFAULT Available
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'AVAILABLE'")
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;
}
