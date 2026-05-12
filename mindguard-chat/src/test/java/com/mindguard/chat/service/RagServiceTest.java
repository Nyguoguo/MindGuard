package com.mindguard.chat.service;

import com.mindguard.chat.service.impl.RagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private VectorStore vectorStore;

    private RagService ragService;

    @BeforeEach
    void setUp() {
        ragService = new RagServiceImpl(vectorStore);
    }

    @Test
    void shouldReturnEmptyWhenNoResults() {
        when(vectorStore.similaritySearch(any(String.class))).thenReturn(List.of());
        assertEquals("", ragService.retrieve("any query"));
    }

    @Test
    void shouldRetrieveRelevantDocs() {
        Document doc1 = new Document("应对考试焦虑可以通过深呼吸放松训练来缓解");
        Document doc2 = new Document("考前保证充足睡眠有助于减轻焦虑");
        when(vectorStore.similaritySearch(any(String.class))).thenReturn(List.of(doc1, doc2));

        String context = ragService.retrieve("考试前很紧张怎么办");
        assertTrue(context.contains("考试焦虑"));
        assertTrue(context.contains("充足睡眠"));
    }

    @Test
    void shouldStoreDocument() {
        ragService.storeDocument("心理危机干预指南", "当学生出现自伤倾向时，应立即介入...");
    }
}
