package com.ll.chat_project.controller;

import com.ll.chat_project.Dto.ChatDto;
import com.ll.chat_project.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRepository repository;

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor){

        //채팅방 유저 +1;
        repository.increaseUser(chat.getRoomId());

        //채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = repository.addUser(chat.getRoomId(), chat.getMessage());

        //반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID",userUUID);
        headerAccessor.getSessionAttributes().put("roomId",chat.getRoomId());

        chat.setMessage(chat.getSender() + "님이 입장하셨습니다.");
        template.convertAndSend("/sub/chat/room/"+chat.getRoomId(),chat);

    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat){

        log.info("chat : {}",chat);
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/room/"+chat.getRoomId(),chat);
    }

    //유저 퇴장 시에는 EventListener 를 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){

        log.info("DisconnectEvent : {}",event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("headAccessor : {}",headerAccessor);

        // 채팅방 유저 -1
        repository.decreaseUser(roomId);

        String userName = repository.getUserName(roomId, userUUID);
        repository.deleteUser(roomId,userUUID);

        if(userName != null){
            log.info("User Disconnected : " + userName);

            ChatDto chat = ChatDto.builder()
                    .type(ChatDto.MessageType.LEAVE)
                    .sender(userName)
                    .message(userName + "님이 퇴장하였습니다.")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId,chat);
        }
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/chat/uselist")
    @ResponseBody
    public List<String> userList(String roomId){

        return repository.getUserList(roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/chat/duplicateName")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId")String roomId ,
                                  @RequestParam("username")String username){

        String userName = repository.isDuplicateName(roomId, username);
        log.info("DuplicateName : {}", userName);

        return userName;
    }


}