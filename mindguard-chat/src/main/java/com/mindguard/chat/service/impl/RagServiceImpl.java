package com.mindguard.chat.service.impl;

import com.mindguard.chat.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagServiceImpl implements RagService {

    private static final int TOP_K = 3;

    private final VectorStore vectorStore;

    public RagServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public String retrieve(String query) {
        List<Document> docs = vectorStore.similaritySearch(query);
        if (docs.isEmpty()) {
            return "";
        }
        return docs.stream()
                .limit(TOP_K)
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public void storeDocument(String title, String content) {
        Document doc = new Document(content);
        doc.getMetadata().put("title", title);
        vectorStore.add(List.of(doc));
        log.info("Document '{}' stored with {} chars", title, content.length());
    }
}
