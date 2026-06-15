package com.zorvyn.assignment.financechatbot.controller;

import com.zorvyn.assignment.financechatbot.dto.ChatRequest;
import com.zorvyn.assignment.financechatbot.dto.ChatResponse;
import com.zorvyn.assignment.financechatbot.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat entry point. Requires a valid Finance API bearer token; the role encoded
 * in that token determines which tools the assistant may use.
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request);
    }
}
