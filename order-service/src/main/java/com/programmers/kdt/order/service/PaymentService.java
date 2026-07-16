package com.programmers.kdt.order.service;

import com.programmers.kdt.order.dto.CreatePaymentRequest;
import com.programmers.kdt.order.dto.CreatePaymentResponse;
import com.programmers.kdt.order.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public CreatePaymentResponse pay(CreatePaymentRequest request) {
        // TODO: PG 연동
        return null;
    }

    public void cancelPayment(Long paymentId) {
        // TODO
    }
}
