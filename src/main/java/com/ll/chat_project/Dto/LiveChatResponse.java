package com.ll.chat_project.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatResponse {
    private String senderName;
    private String senderId;
    private String message;
}
