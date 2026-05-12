package com.mindguard.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.chat.entity.Conversation;
import com.mindguard.chat.entity.PsychReport;
import com.mindguard.chat.mapper.ConversationMapper;
import com.mindguard.chat.model.ModelStrategy;
import com.mindguard.chat.service.ChatService;
import com.mindguard.chat.service.PsychAnalysisService;
import com.mindguard.chat.service.RagService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private static final String SYSTEM_PROMPT =
            "你是一位专业且友善的心理咨询助手，请遵循以下原则：\n" +
            "1. 使用共情和积极倾听的技巧\n" +
            "2. 不提供医疗诊断，必要时建议寻求专业帮助\n" +
            "3. 保持温和、不评判的态度\n" +
            "4. 如果对方表达出自伤或自杀倾向，请严肃对待并建议立即联系心理危机热线";

    private final ConversationMapper conversationMapper;
    private final ModelStrategy modelStrategy;
    private final RagService ragService;
    private final PsychAnalysisService psychAnalysisService;

    public ChatServiceImpl(ConversationMapper conversationMapper, ModelStrategy modelStrategy,
                           RagService ragService, PsychAnalysisService psychAnalysisService) {
        this.conversationMapper = conversationMapper;
        this.modelStrategy = modelStrategy;
        this.ragService = ragService;
        this.psychAnalysisService = psychAnalysisService;
    }

    @Override
    public Flux<String> streamChat(String sessionId, Long userId, String message) {
        saveMessage(sessionId, userId, "user", message);

        String knowledgeContext = ragService.retrieve(message);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt(knowledgeContext)));

        List<Conversation> history = getHistoryInternal(sessionId, 10);
        for (Conversation c : history) {
            messages.add(Map.of("role", c.getRole(), "content", c.getContent()));
        }
        messages.add(Map.of("role", "user", "content", message));

        StringBuilder fullResponse = new StringBuilder();

        return modelStrategy.chat(messages)
                .doOnNext(fullResponse::append)
                .doOnComplete(() -> {
                    saveMessage(sessionId, userId, "assistant", fullResponse.toString());
                    analyzeAsync(sessionId, userId, message, fullResponse.toString());
                });
    }

    private void analyzeAsync(String sessionId, Long userId, String userMsg, String assistantMsg) {
        String combined = userMsg + "\n" + assistantMsg;
        Mono.fromRunnable(() -> psychAnalysisService.analyze(sessionId, userId, combined))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Override
    public List<Conversation> getHistory(String sessionId) {
        return getHistoryInternal(sessionId, 50);
    }

    private List<Conversation> getHistoryInternal(String sessionId, int limit) {
        return conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getSessionId, sessionId)
                        .orderByDesc(Conversation::getCreatedAt)
                        .last("LIMIT " + limit));
    }

    private void saveMessage(String sessionId, Long userId, String role, String content) {
        Conversation conv = new Conversation();
        conv.setSessionId(sessionId);
        conv.setUserId(userId);
        conv.setRole(role);
        conv.setContent(content);
        conversationMapper.insert(conv);
    }

    private String buildSystemPrompt(String knowledgeContext) {
        if (knowledgeContext != null && !knowledgeContext.isEmpty()) {
            return SYSTEM_PROMPT + "\n\n参考知识库:\n" + knowledgeContext;
        }
        return SYSTEM_PROMPT;
    }
}
