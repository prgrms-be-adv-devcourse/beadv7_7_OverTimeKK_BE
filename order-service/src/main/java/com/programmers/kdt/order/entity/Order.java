package com.programmers.kdt.order.entity;

import com.programmers.kdt.common.entity.BaseTimeEntity;
import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.common.exception.OrderErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private Long orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDate orderDate;


    private Order(Long userId, Long ticketId, Long orderAmount){
        validateOrder(userId, ticketId, orderAmount);
        this.userId = userId;
        this.ticketId = ticketId;
        this.orderAmount = orderAmount;
        this.orderStatus = OrderStatus.PENDING;
        this.orderDate = LocalDate.now();
    }

    // 주문 생성
    public static Order create(Long userId, Long ticketId, Long orderAmount){
        return new Order(userId, ticketId, orderAmount);
    }

    // 주문 검증
    private void validateOrder(Long userId, Long ticketId, Long orderAmount){
        if(userId == null){
            throw new BusinessException(OrderErrorCode.USER_ID_REQUIRED); // *** USERERRORCODE에 있어야 하는지 확인
        }
        if(ticketId == null){
            throw new BusinessException(OrderErrorCode.TICKET_ID_REQUIRED); // *** TICKETERRORCODE에 있어야 하는지 확인
        }
        if(orderAmount == null || orderAmount <= 0){
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_AMOUNT);
        }
    }


    // 주문 완료 PENDING -> COMPLETED
    public void complete(){
        if(orderStatus != OrderStatus.PENDING){
            throw new BusinessException(OrderErrorCode.ORDER_NOT_PENDING);
        }
        this.orderStatus = OrderStatus.COMPLETED;
    }

    // 주문 만료 PENDING -> EXPIRED
    public void expire(){
        if(orderStatus != OrderStatus.PENDING){
            throw new BusinessException(OrderErrorCode.ORDER_NOT_PENDING);
        }
        this.orderStatus = OrderStatus.EXPIRED;
    }

    // 주문 취소 COMPLETED -> CANCELED
    public void cancel(){
        if(orderStatus != OrderStatus.COMPLETED){
            throw new BusinessException(OrderErrorCode.ORDER_NOT_COMPLETED);
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }

}
