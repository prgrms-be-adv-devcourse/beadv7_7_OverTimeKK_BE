package com.programmers.kdt.order.repository;

import com.programmers.kdt.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
