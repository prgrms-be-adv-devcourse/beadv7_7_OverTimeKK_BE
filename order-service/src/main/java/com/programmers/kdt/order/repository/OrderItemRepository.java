package com.programmers.kdt.order.repository;

import com.programmers.kdt.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findFirstByOrderId(Long orderId);
}
