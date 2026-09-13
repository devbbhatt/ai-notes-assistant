package com.example.ai_notes_assistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class NoteService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final TokenTextSplitter textSplitter;

    public NoteService(VectorStore vectorStore,
                       ChatClient.Builder chatClientBuilder) {

        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        this.textSplitter = new TokenTextSplitter();
    }

    public String addDocument(String title, String content) {

        Document document = new Document(
                content,
                java.util.Map.of(
                        "title", title,
                        "source", "AI Notes Assistant"
                )
        );

        List<Document> chunks = textSplitter.apply(List.of(document));

        vectorStore.add(chunks);

        return "Document added successfully. Chunks created: " + chunks.size();
    }

    public String addPdf(MultipartFile file) {

        try {
            PagePdfDocumentReader pdfReader =
                    new PagePdfDocumentReader(
                            new InputStreamResource(file.getInputStream())
                    );

            List<Document> documents = pdfReader.get();

            List<Document> chunks = textSplitter.apply(documents);

            vectorStore.add(chunks);

            return "PDF uploaded successfully. Chunks created: " + chunks.size();

        } catch (Exception e) {
            return "Failed to process PDF: " + e.getMessage();
        }
    }

    private List<Document> retrieveRelevantDocuments(String question) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }

    @Tool(description = "Search the user's study notes for relevant information")
    public String searchNotes(String question) {

        System.out.println("===== TOOL CALLED: searchNotes =====");
        System.out.println("Question sent to tool: " + question);

        List<Document> results =
                retrieveRelevantDocuments(question);

        if (results.isEmpty()) {
            return "No relevant information found in the user's notes.";
        }

        return results.stream()
                .map(document -> {
                    String title = String.valueOf(
                            document.getMetadata().getOrDefault("title", "Unknown")
                    );

                    return "Source: " + title + "\n" + document.getText();
                })
                .collect(Collectors.joining("\n---\n"));
    }

    public String search(String question) {

        List<Document> results =
                retrieveRelevantDocuments(question);

        if (results.isEmpty()) {
            return "No relevant document found";
        }

        return results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
    }

    public String ask(String question) {

        return chatClient
                .prompt("""
                    You are a helpful study assistant.

                    Answer the user's question using the user's study notes.

                    If the information is not available in the notes,
                    say:
                    "I could not find relevant information in your notes."

                    Do not use outside knowledge.

                    Question:
                    %s
                    """.formatted(question))
                .tools(this)
                .call()
                .content();
    }


}
