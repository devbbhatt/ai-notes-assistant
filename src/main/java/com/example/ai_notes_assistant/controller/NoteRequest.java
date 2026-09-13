package com.example.ai_notes_assistant.controller;

public record NoteRequest(
        String title,
        String content
) {
}