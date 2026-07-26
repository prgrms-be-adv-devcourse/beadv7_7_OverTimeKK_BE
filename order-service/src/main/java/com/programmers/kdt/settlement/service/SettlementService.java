package com.programmers.kdt.settlement.service;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.settlement.dto.SessionSalesAmount;
import com.programmers.kdt.settlement.dto.SessionSettlementAmount;
import com.programmers.kdt.settlement.entity.Settlement;
import com.programmers.kdt.settlement.entity.SettlementDetail;
import com.programmers.kdt.settlement.exception.SettlementErrorCode;
import com.programmers.kdt.settlement.repository.SettlementDetailRepository;
import com.programmers.kdt.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final BigDecimal pgFeeRate = BigDecimal.valueOf(0.0374);
    private final BigDecimal serviceFeeRate = BigDecimal.valueOf(0.05);

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;

    @Transactional
    public void createSettlement(
            Long sellerId,
            LocalDate settlementMonth,
            LocalDate scheduledSettlementDate,
            List<SessionSalesAmount> salesAmounts
            ){
        if(salesAmounts == null || salesAmounts.isEmpty()){
            return;
        }
        List<SessionSettlementAmount> sessionAmounts = salesAmounts.stream()
                .map(this::calculateSessionAmount)
                .toList();
        Long totalGrossAmount = sessionAmounts.stream()
                .mapToLong(SessionSettlementAmount::grossAmount)
                .sum();
        Long totalPgFeeAmount = sessionAmounts.stream()
                .mapToLong(SessionSettlementAmount::pgFeeAmount)
                .sum();
        Long totalServiceFeeAMount = sessionAmounts.stream()
                .mapToLong(SessionSettlementAmount::serviceFeeAmount)
                .sum();

        Settlement settlement = Settlement.create(sellerId,settlementMonth, scheduledSettlementDate, totalGrossAmount, totalPgFeeAmount, totalServiceFeeAMount);
        Settlement savedSettlement = settlementRepository.save(settlement);

        List<SettlementDetail> details = sessionAmounts.stream()
                .map(session -> SettlementDetail.create(
                        savedSettlement,
                        session.performanceId(),
                        session.sessionNum(),
                        session.grossAmount(),
                        session.pgFeeAmount(),
                        session.serviceFeeAmount()
                ))
                .toList();
        settlementDetailRepository.saveAll(details);
    }

    private SessionSettlementAmount calculateSessionAmount(SessionSalesAmount salesAmount){
        validateGrossAmount(salesAmount.grossAMount());
        Long pgFeeAmount = calculateFee(salesAmount.grossAMount(), pgFeeRate);
        Long serviceFeeAmount = calculateFee(salesAmount.grossAMount(), serviceFeeRate);
        return new SessionSettlementAmount(
                salesAmount.performacneId(),
                salesAmount.sessionNum(),
                salesAmount.grossAMount(),
                pgFeeAmount,
                serviceFeeAmount
        );
    }

    private long calculateFee(Long amount, BigDecimal feeRate){
        return BigDecimal.valueOf(amount)
                .multiply(feeRate)
                .setScale(0, RoundingMode.DOWN)
                .longValueExact();
    }

    private void validateGrossAmount(Long grossAmount){
        if(grossAmount == null || grossAmount < 0){
            throw new BusinessException(SettlementErrorCode.INVALID_SETTLEMENT_AMOUNT);
        }
    }
}
