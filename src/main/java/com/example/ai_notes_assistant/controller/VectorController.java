package com.example.ai_notes_assistant.controller;

import com.example.ai_notes_assistant.service.NoteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VectorController {

    private final NoteService noteService;

    public VectorController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/add")
    public String addDocument(@RequestBody NoteRequest request) {
        return noteService.addDocument(request.title(), request.content());
    }

    @GetMapping("/search")
    public String search(@RequestParam String question) {
        return noteService.search(question);
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return noteService.ask(question);
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {
        return noteService.addPdf(file);
    }


}