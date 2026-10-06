package com.project.spring_ai_rag.rag;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HRPolicyLoader {
    @Value("classpath:/misc/Eazybytes_HR_Policies.pdf")
    private String pdfFilePath;
    private VectorStore vectorStore;

    public HRPolicyLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    protected void loadPDF(){
        /*
        Apache Tika is a library that can be used to extract text from PDF files.
        You can use it to read the content of the PDF and then split it into sentences or paragraphs
        before adding them to the vector store.
          - Split into sentences if you want more granular search results.
         -  Split into paragraphs if you want to preserve more context in each chunk.
         -  For best of both worlds, you can split into paragraphs and then further split long paragraphs into sentences.
         */

        TikaDocumentReader tikaDocumentLoader = new TikaDocumentReader(pdfFilePath);
        List<Document> documents = tikaDocumentLoader.get();
        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(100) // Adjust the chunk size as needed
                // Every small document will be split into chunks of 100 tokens each.
                // You can adjust this based on your use case and the capabilities of your vector store.
                .withMaxNumChunks(400) // Adjust the maximum number of chunks as needed
                // A single document should not be split into more than 400 chunks.
                // This is to prevent excessive splitting of very long documents.
                .build();
        vectorStore.add(textSplitter.split(documents));
    }

}
