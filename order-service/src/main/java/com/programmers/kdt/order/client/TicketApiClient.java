package com.programmers.kdt.order.client;

import com.programmers.kdt.common.exception.BusinessException;
import com.programmers.kdt.common.response.ApiResponse;
import com.programmers.kdt.order.dto.TicketHoldRequest;
import com.programmers.kdt.order.dto.TicketHoldResult;
import com.programmers.kdt.order.dto.TicketReleaseRequest;
import com.programmers.kdt.order.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class TicketApiClient implements TicketClient {

    private final RestClient restClient;

    @Override
    public TicketHoldResult holdSeat(TicketHoldRequest ticketRequest){
        try{
            ApiResponse<TicketHoldResult> response = restClient.put()
                    .uri("/api/tickets/status/hold")
                    .body(ticketRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<TicketHoldResult>>() {});
            if(response == null || response.data() == null){
                throw new BusinessException(OrderErrorCode.TICKET_HOLD_RESPONSE_EMPTY);
            }
            return response.data();

        } catch (RestClientResponseException e){
            throw new BusinessException(OrderErrorCode.TICKET_HOLD_FAILED);
        }
    }

    @Override
    public void releaseSeat(Long ticketId, TicketReleaseRequest request){
        restClient.put()
                .uri("/api/tickets/status/{ticketId}/release", ticketId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public TicketInfo getTicket(Long ticketId){
        return new TicketInfo(
                ticketId,
                "오페라의 유령",
                "R"
        );
    }
}
