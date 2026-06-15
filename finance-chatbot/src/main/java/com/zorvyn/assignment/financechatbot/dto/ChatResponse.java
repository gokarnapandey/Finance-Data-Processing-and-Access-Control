package com.zorvyn.assignment.financechatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String conversationId;
    private String reply;
}
