package com.programmers.kdt.payment.controller;

import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.payment.dto.ConfirmPaymentRequest;
import com.programmers.kdt.payment.dto.ConfirmPaymentResponse;
import com.programmers.kdt.payment.dto.CreatePaymentRequest;
import com.programmers.kdt.payment.dto.CreatePaymentResponse;
import com.programmers.kdt.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatePaymentResponse> pay(@Valid @RequestBody CreatePaymentRequest request) {
        CreatePaymentResponse response = paymentService.pay(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/{paymentId}/confirm")
    public ApiResponse<ConfirmPaymentResponse> confirm(
            @PathVariable Long paymentId,
            @RequestBody ConfirmPaymentRequest request
    ) {
        ConfirmPaymentResponse response = paymentService.confirm(paymentId, request);
        return ApiResponse.success(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public void cancel(@PathVariable Long paymentId) {

        paymentService.cancelPayment(paymentId);
    }
}
