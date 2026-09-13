package com.example.ai_notes_assistant.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String question) {

        return chatClient
                .prompt(question)  // -> ChatClient ko bolo ki ye prompt AI ko do
                .call() // → AI ko call karo
                .content(); // → response ka content mujhe de do.

    }
}
