package com.project.spring_ai_rag.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RandomDataLoader {
    private VectorStore vectorStore;

    protected RandomDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct // This method is called after the bean is constructed and dependencies are injected
    public void loadSentenccesIntoVectorStore() {
        List<String> sentences = List.of(
                "Bitcoin’s supply is capped at 21 million coins.",
                "A blockchain is a shared ledger made of linked data blocks.",
                "Public-key cryptography uses a public key and a private key.",
                "Hash functions produce fixed-length outputs from input data.",
                "Ethereum introduced programmable smart contracts to a large audience.",
                "A blockchain transaction is generally irreversible once sufficiently confirmed.",
                "Proof of work relies on computational effort to secure a network.",
                "Proof of stake selects validators based partly on locked cryptocurrency.",
                "A seed phrase can restore access to a crypto wallet.",
                "Never share a wallet’s private key or seed phrase.",
                "The first computer bug was famously a moth found in the Harvard Mark II.",
                "The term algorithm comes from the name of mathematician al-Khwarizmi.",
                "Binary uses only two digits: 0 and 1.",
                "One byte consists of eight bits.",
                "DNS translates domain names into IP addresses.",
                "HTTP is commonly used to transfer web content.",
                "Git tracks changes in source code over time.",
                "SQL is used to query and manage relational databases.",
                "Big O notation describes how an algorithm’s resource usage grows with input size.",
                "A hash table usually offers near-constant-time lookups.",
                "The Pacific Ocean is Earth’s largest and deepest ocean.",
                "Antarctica is the coldest continent.",
                "The Sahara is the world’s largest hot desert.",
                "Mount Everest is the highest mountain above sea level.",
                "The Amazon rainforest spans multiple South American countries.",
                "Earth’s atmosphere is mostly nitrogen.",
                "Oceans cover roughly 71% of Earth’s surface.",
                "A day on Venus is longer than a year on Venus.",
                "The Moon is gradually moving farther away from Earth.",
                "Honey can remain edible for extremely long periods when properly sealed.",
                "Octopuses have three hearts.",
                "Bananas are botanically berries, while strawberries are not.",
                "Lightning can heat the surrounding air to temperatures hotter than the Sun’s surface.",
                "A group of flamingos is called a flamboyance.",
                "Sharks existed before trees appeared on Earth.",
                "The human brain contains billions of neurons.",
                "DNA stores genetic instructions for living organisms.",
                "Sound travels faster through water than through air.",
                "The Great Barrier Reef is the world’s largest coral reef system.",
                "The Nile is commonly recognized as one of the world’s longest rivers."
        );

        List<Document> listOfDocument = sentences.stream().map(Document::new).collect(Collectors.toList());
        vectorStore.add(listOfDocument);
    }
}
