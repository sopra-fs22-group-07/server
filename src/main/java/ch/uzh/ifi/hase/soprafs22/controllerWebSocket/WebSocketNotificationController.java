package ch.uzh.ifi.hase.soprafs22.controllerWebSocket;

import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.Principal;

public class WebSocketNotificationController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/notification")
    public void sendSpecific(
            @Payload Long userId,
            Principal user) throws Exception {
        String out = "Hello";
        System.out.print("out: "+ out);
        String id = String.valueOf(userId);
        simpMessagingTemplate.convertAndSendToUser(
                id, "/user/queue/specific-user", out);
    }
}
