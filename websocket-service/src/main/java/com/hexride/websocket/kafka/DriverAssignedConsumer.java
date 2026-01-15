package com.hexride.websocket.kafka;

import com.hexride.common.event.DriverAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverAssignedConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "driver.assigned", groupId = "websocket-service")
    public void handleDriverAssigned(DriverAssignedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/ride/" + event.getRideId() + "/driver-assigned",
                event
        );

        if (event.getRiderId() != null) {
            messagingTemplate.convertAndSendToUser(
                    event.getRiderId(),
                    "/queue/driver-assigned",
                    event
            );
        }

        log.info("Driver assigned notification sent for ride: {}", event.getRideId());
    }
}
