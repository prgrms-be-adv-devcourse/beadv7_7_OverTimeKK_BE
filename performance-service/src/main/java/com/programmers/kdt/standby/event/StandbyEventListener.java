package com.programmers.kdt.standby.event;

import com.programmers.kdt.standby.service.StandbyService;
import com.programmers.kdt.ticket.event.TryMatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StandbyEventListener {

    private final StandbyService standbyService;

    @EventListener
    public void handle(TryMatchEvent event) {
        // TODO : TryMatch 시에 실행 될 이벤트 작성 필요ø
    }
}
