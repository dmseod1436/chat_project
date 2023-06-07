package com.ll.chat_project.Dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChatDto {

    // 메시지 타입 :  입장 채팅 퇴장
    // ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
    // TALK 는 해당 채팅방을 sub 하고 있는 모든 클라이언트에게 전달됩니다.
    public enum MessageType{
        ENTER, TALK, LEAVE
    }
    private MessageType type; //메시지 타입
    private String roomId;// 방 번호
    private String sender;// 채팅을 보낸 사람
    private String message;// 메세지
    private String chatTime; // 채팅 발송 시간

}