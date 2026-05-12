package com.mindguard.chat.service;

import com.mindguard.chat.entity.Conversation;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Flux<String> streamChat(String sessionId, Long userId, String message);
    List<Conversation> getHistory(String sessionId);
}
