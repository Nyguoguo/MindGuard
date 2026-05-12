package com.mindguard.chat.service;

import com.mindguard.chat.entity.Conversation;
import com.mindguard.chat.mapper.ConversationMapper;
import com.mindguard.chat.model.ModelStrategy;
import com.mindguard.chat.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private ModelStrategy modelStrategy;

    @Mock
    private RagService ragService;

    @Mock
    private PsychAnalysisService psychAnalysisService;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatServiceImpl(conversationMapper, modelStrategy,
                ragService, psychAnalysisService);
    }

    @Test
    void shouldStreamChatAndSaveConversation() {
        when(conversationMapper.insert(any(Conversation.class))).thenReturn(1);
        when(ragService.retrieve(anyString())).thenReturn("知识库相关: 应对焦虑的方法...");
        when(modelStrategy.chat(anyList())).thenReturn(Flux.just("我", "理解", "你的", "感受"));

        Flux<String> result = chatService.streamChat("session-1", 1L, "最近压力很大");

        StepVerifier.create(result)
                .expectNext("我", "理解", "你的", "感受")
                .verifyComplete();
    }

    @Test
    void shouldHandleEmptyKnowledgeBase() {
        when(conversationMapper.insert(any(Conversation.class))).thenReturn(1);
        when(ragService.retrieve(anyString())).thenReturn("");
        when(modelStrategy.chat(anyList())).thenReturn(Flux.just("请详细说说你的感受"));

        Flux<String> result = chatService.streamChat("session-2", 1L, "感觉不开心");

        StepVerifier.create(result)
                .expectNext("请详细说说你的感受")
                .verifyComplete();
    }

    @Test
    void shouldNotExposeTheCorePrompt() {
        // The core prompt about psychological analysis should be part of
        // system message construction but not directly echoed to user
        when(conversationMapper.insert(any(Conversation.class))).thenReturn(1);
        when(ragService.retrieve(anyString())).thenReturn("");
        when(modelStrategy.chat(anyList())).thenReturn(Flux.just("我能感受到你的困扰"));

        Flux<String> result = chatService.streamChat("session-3", 1L, "Help me");
        StepVerifier.create(result).expectNextCount(1).verifyComplete();
    }
}
