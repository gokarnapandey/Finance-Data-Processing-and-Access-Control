package com.zorvyn.assignment.financechatbot.service;

import com.zorvyn.assignment.financechatbot.dto.ChatRequest;
import com.zorvyn.assignment.financechatbot.dto.ChatResponse;
import com.zorvyn.assignment.financechatbot.security.CurrentToken;
import com.zorvyn.assignment.financechatbot.security.Role;
import com.zorvyn.assignment.financechatbot.tools.ToolRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates a single chat turn: selects the tools allowed for the caller's
 * role, attaches the conversation memory thread, and runs the Spring AI tool
 * calling loop against the local Ollama model.
 */
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ToolRegistry toolRegistry;
    private final CurrentToken currentToken;

    public ChatService(ChatClient chatClient, ToolRegistry toolRegistry, CurrentToken currentToken) {
        this.chatClient = chatClient;
        this.toolRegistry = toolRegistry;
        this.currentToken = currentToken;
    }

    public ChatResponse chat(ChatRequest request) {
        Role role = currentToken.getRole();
        String user = currentToken.getUsername() != null ? currentToken.getUsername() : "anonymous";
        String conversationId = (request.getConversationId() != null && !request.getConversationId().isBlank())
                ? request.getConversationId()
                : user;

        String reply = chatClient.prompt()
                .system(systemPrompt(role, user))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .tools(toolRegistry.toolsFor(role))
                .user(request.getMessage())
                .call()
                .content();

        return new ChatResponse(conversationId, reply);
    }

    private String systemPrompt(Role role, String user) {
        return """
                You are FinBot, an assistant for the Finance Record Management system.
                The current user is '%s' with role '%s'.

                Rules:
                - Always use the provided tools to read or change data. Never invent records,
                  amounts, ids, categories, dates, or users.
                - You are only given the tools appropriate for the user's role. If the user asks
                  for something you have no tool for, explain that their role does not permit it
                  rather than guessing.
                - Valid categories: FOOD, RENT, TRANSPORT, ENTERTAINMENT, SALARY, INVESTMENT, OTHER.
                  Valid transaction types: INCOME, EXPENSE.
                - If a tool result starts with "ERROR", do not retry blindly. Read the status:
                  403 means not permitted, 404 means not found, 400 means invalid input. Explain
                  the problem to the user in plain language.
                - Before creating, updating, or deleting anything, make sure you have the required
                  details; ask a brief clarifying question if something essential is missing.
                - Be concise. Summarise JSON results in clear prose and format money readably.
                """.formatted(user, role.name());
    }
}
