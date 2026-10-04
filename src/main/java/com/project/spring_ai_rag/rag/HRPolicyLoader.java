package com.project.spring_ai_rag.rag;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HRPolicyLoader {
    @Value("classpath:/resources/misc/Eazybytes_HR_Policies.pdf")
    private String pdfFilePath;
    private VectorStore vectorStore;

    public HRPolicyLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    private void loadPDF(){
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
        vectorStore.add(documents);
    }

}
