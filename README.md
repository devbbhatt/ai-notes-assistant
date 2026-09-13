# AI Notes Assistant

An AI-powered study assistant built with Spring Boot, Spring AI, Ollama, PostgreSQL and PGVector.

The application allows users to upload study notes and PDF documents and ask questions about their content. It uses RAG (Retrieval-Augmented Generation) and AI Tool Calling to retrieve relevant information from the uploaded notes before generating an answer.

## Features

- Upload PDF study material
- Add text-based study notes
- Automatically split documents into smaller chunks
- Generate embeddings using Ollama
- Store document embeddings in PostgreSQL with PGVector
- Perform semantic similarity search
- Ask questions about uploaded notes
- AI Tool Calling using Spring AI
- Retrieve relevant notes automatically when answering questions
- REST APIs for adding, searching and asking questions

## Architecture

~~~text
                         User
                           |
                           v
                  Spring Boot REST API
                           |
              +------------+------------+
              |                         |
              v                         v
         Upload PDF                Ask Question
              |                         |
              v                         v
          PDF Reader                ChatClient
              |                         |
              v                         v
       Text Splitting            Ollama Qwen Model
              |                         |
              v                         |
          Embeddings                    |
              |                         |
              v                         |
     PostgreSQL + PGVector              |
              ^                         |
              |                         |
              +---- Semantic Search ----+
                           |
                           v
                  Relevant Note Chunks
~~~

## How the Project Works

### 1. Document Upload

When a PDF is uploaded, the application extracts its text using Spring AI PDF document readers.

The extracted document is divided into smaller chunks using `TokenTextSplitter`.

### 2. Embeddings

Each document chunk is converted into a numerical vector called an embedding.

The project uses the Ollama embedding model:

`nomic-embed-text`

### 3. Vector Storage

The generated embeddings and document information are stored in PostgreSQL using PGVector.

PGVector allows the application to perform similarity searches on the stored document embeddings.

### 4. Asking a Question

When a user asks a question, the question is sent to the AI model.

The AI model has access to a custom tool called `searchNotes`.

The model can call this tool when it needs information from the user's study notes.

### 5. Semantic Search

The `searchNotes` tool performs a similarity search in PGVector.

It retrieves the most relevant document chunks related to the user's question.

### 6. AI Answer

The retrieved information is returned to the AI model.

The model then generates the final answer using the retrieved study material.

This is the RAG flow used in the project.

## RAG Flow

~~~text
Question
   |
   v
AI Model
   |
   | Tool Call
   v
searchNotes
   |
   v
Vector Similarity Search
   |
   v
PGVector
   |
   v
Relevant Document Chunks
   |
   v
AI Model
   |
   v
Final Answer
~~~

## AI Tool Calling

Spring AI Tool Calling is used to allow the AI model to interact with the application's search functionality.

The `searchNotes` method is exposed as a tool:

~~~java
@Tool(description = "Search the user's study notes for relevant information")
public String searchNotes(String question)
~~~

The AI model can decide when to call this tool to retrieve information from the user's notes.

This makes the application more agent-like because the model can choose to use the available tool instead of the application manually performing the search every time.

## Technologies Used

- Java 21
- Spring Boot
- Spring AI
- Ollama
- Qwen 2.5 3B
- Nomic Embed Text
- PostgreSQL
- PGVector
- Maven
- REST API

## Ollama Models

### Chat Model

~~~text
qwen2.5:3b
~~~

### Embedding Model

~~~text
nomic-embed-text
~~~

Required Ollama commands:

~~~bash
ollama pull qwen2.5:3b
ollama pull nomic-embed-text
~~~

## API Endpoints

### Add Notes

~~~http
POST /add
~~~

Adds study notes to the vector store.

Example request:

~~~json
{
  "title": "Computer Networks",
  "content": "CRC stands for Cyclic Redundancy Check. It is an error detection technique."
}
~~~

### Search Notes

~~~http
GET /search?question=What%20is%20CRC?
~~~

This endpoint directly performs a semantic similarity search and returns relevant note content.

### Ask Question

~~~http
GET /ask?question=What%20is%20CRC?
~~~

This endpoint sends the question to the AI model.

The AI model can use the `searchNotes` tool to retrieve relevant information from the stored notes.

### Upload PDF

~~~http
POST /upload
~~~

The PDF is processed, split into chunks and stored in the vector database.

## Example

Suppose a Java PDF is uploaded containing information about inheritance.

The user asks:

~~~text
What is inheritance in Java?
~~~

The application performs the following steps:

1. The AI receives the question.
2. The AI decides whether it needs information from the study notes.
3. The AI calls the `searchNotes` tool.
4. `searchNotes` performs a similarity search in PGVector.
5. Relevant chunks from the uploaded PDF are retrieved.
6. The retrieved information is provided to the AI.
7. The AI generates the final answer.

Example flow:

~~~text
User Question
      |
      v
AI Model
      |
      v
searchNotes Tool
      |
      v
PGVector Similarity Search
      |
      v
Relevant PDF Content
      |
      v
AI Model
      |
      v
Final Answer
~~~

## Project Structure

~~~text
ai-notes-assistant
|
+-- src
|   |
|   +-- main
|   |   |
|   |   +-- java
|   |   |   |
|   |   |   +-- controller
|   |   |   |   |
|   |   |   |   +-- ChatController.java
|   |   |   |   +-- EmbeddingController.java
|   |   |   |   +-- NoteRequest.java
|   |   |   |   +-- VectorController.java
|   |   |   |
|   |   |   +-- service
|   |   |       |
|   |   |       +-- NoteService.java
|   |   |
|   |   +-- resources
|   |       |
|   |       +-- application.properties
|   |
|   +-- test
|
+-- pom.xml
+-- README.md
+-- mvnw
+-- mvnw.cmd
~~~

## Requirements

Before running the project, install:

- Java 21
- Maven
- PostgreSQL
- PGVector
- Ollama

Make sure PostgreSQL and Ollama are running.

Also make sure the required Ollama models are available:

~~~bash
ollama list
~~~

Required models:

~~~text
qwen2.5:3b
nomic-embed-text
~~~

## Running the Project

Clone the repository:

~~~bash
git clone https://github.com/devbbhatt/ai-notes-assistant.git
~~~

Open the project:

~~~bash
cd ai-notes-assistant
~~~

Start the application on Windows:

~~~bash
mvnw.cmd spring-boot:run
~~~

On Linux or macOS:

~~~bash
./mvnw spring-boot:run
~~~

The application will run on:

~~~text
http://localhost:8080
~~~

## Learning Goals

This project was built as a practical learning project to understand:

- Spring AI
- RAG
- Vector databases
- Embeddings
- Semantic search
- Ollama integration
- AI Tool Calling
- PDF document processing
- Spring Boot REST APIs
- Integration of AI models with backend applications

## Future Improvements

- Web frontend for PDF upload and chat
- Multiple document management
- Conversation memory
- Page number source references
- User authentication
- User-specific document storage
- Streaming AI responses
- Support for additional document formats

## Author

**Dev Bhatt**

GitHub:

https://github.com/devbbhatt