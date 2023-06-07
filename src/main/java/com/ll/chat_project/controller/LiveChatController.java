package com.ll.chat_project.controller;

import com.ll.chat_project.Dto.LiveChatRequest;
import com.ll.chat_project.Dto.LiveChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LiveChatController {

    @MessageMapping("/live/chats/{category}/sendMessage")
    @SendTo("/sub/live/chats/{category}")
    public LiveChatResponse sendChatMessage(@DestinationVariable String category, LiveChatRequest request,
                                            @AuthenticationPrincipal User user)  {
        return LiveChatResponse.builder()
                .senderName(user.getUsername())
                .message(request.getMessage())
                .build();
    }

    @GetMapping("/live/chats/{category}")
    public String showLiveChat(@PathVariable String category) {
        return "room";
    }

}
