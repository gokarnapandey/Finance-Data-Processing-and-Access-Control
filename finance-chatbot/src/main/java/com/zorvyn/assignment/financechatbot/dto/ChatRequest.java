package com.zorvyn.assignment.financechatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    /** Optional conversation/thread id. If omitted, the user's identity is used as the thread. */
    private String conversationId;

    @NotBlank(message = "message must not be blank")
    private String message;
}
