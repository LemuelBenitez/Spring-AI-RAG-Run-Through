package com.project.spring_ai_rag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import com.project.spring_ai_rag.rag.RandomDataLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Slf4j
@RestController
public class RAGController {
    @Value("classpath:/promptTemlates/systemPromptRandomDataTemplate.st")
    private Resource assetPromptResource;
    @Value("classpath:/promptTemlates/hrSystemTemplate.st")
    private Resource hrSystemPromptResource;
    private final VectorStore vectorStore;

    private ChatClient chatClient;
    private RandomDataLoader randomDataLoader;

    public RAGController(@Qualifier("ollamaChatClient") ChatClient.Builder chatClient,
                         RandomDataLoader randomDataLoader, VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.randomDataLoader = randomDataLoader;
        // Not needed to call loadSentencesIntoVectorStore() here, as it is already called in the
        // @PostConstruct method of RandomDataLoader -> else will load the same data multiple times into the vector store, which is not efficient
        this.vectorStore = vectorStore;
    }

    @GetMapping("/basic-rag-chat")
    public String getRAGResponse(@RequestHeader("username")String username,
                                 @RequestParam String userInput) {
        // 1. Load random data into the vector store
        // This ensures that the vector store is populated with relevant documents for the RAG response
        // randomDataLoader.loadSentenccesIntoVectorStore(); - this is already done in the @PostConstruct method of RandomDataLoader, so no need to call it here
        //2. Perform a similarity search in the vector store to find relevant documents based on user input
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userInput)
                .topK(3) // Retrieve the top 3 relevant documents
                .similarityThreshold(0.5)
                // Set a similarity threshold for filtering results
                // This helps to ensure that only relevant documents are considered for the RAG response
                // Accepts a similarity threshold between 0.0 and 1.0, where 1.0 means only exact matches are considered
                //.filterExpression("similarity >= 0.5")
                // This is an alternative way to filter results based on similarity
                // Use the filterExpression to apply more complex filtering logic if needed
                .build();
       List<Document> relevantDocuments =  vectorStore.similaritySearch(searchRequest);
        //3. Use the retrieved documents to construct a context for the RAG response
        String similarContext = relevantDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        //4. Construct the prompt for the chat model using the user input and the context from the retrieved documents
        String prompt = String.format("User Input: %s Context: %s", userInput, similarContext);
        log.info(prompt);
        //5. Call the chat model with the constructed prompt to generate a RAG response
        return this.chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(assetPromptResource)
                        .param("documents", similarContext))
                .advisors(advisor -> advisor.param(CONVERSATION_ID, username))
                .user(userInput)
                .call().content();
    }


    @GetMapping("/rag-with-document-chat")
    public String getRAGResponseWithDocuments(@RequestHeader("username")String username,
                                 @RequestParam String userInput) {
        // Reminder: we can use prompt stuffing, but when we are dealing with large documents,
        // we should use RAG to retrieve relevant information from the document and then use that information
        // to generate a response.
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userInput)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> relevantDocuments =  vectorStore.similaritySearch(searchRequest);
        //3. Use the retrieved documents to construct a context for the RAG response
        String similarContext = relevantDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        //4. Construct the prompt for the chat model using the user input and the context from the retrieved documents
        String prompt = String.format("User Input: %s Context: %s", userInput, similarContext);
        log.info(prompt);
        //5. Call the chat model with the constructed prompt to generate a RAG response
        return this.chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(hrSystemPromptResource)
                        .param("documents", similarContext))
                .advisors(advisor -> advisor.param(CONVERSATION_ID, username))
                .user(userInput)
                .call().content();
    }
}
