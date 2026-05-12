package com.mindguard.chat.model.impl;

import com.mindguard.chat.model.ModelStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "mindguard.model.strategy", havingValue = "openai")
public class OpenAIChatService implements ModelStrategy {

    private final ChatClient chatClient;

    public OpenAIChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Flux<String> chat(List<Map<String, String>> messages) {
        var prompt = chatClient.prompt();
        for (Map<String, String> msg : messages) {
            switch (msg.get("role")) {
                case "system" -> prompt.system(msg.get("content"));
                case "user" -> prompt.user(msg.get("content"));
                default -> prompt.user(msg.get("content"));
            }
        }
        return prompt.stream().content();
    }
}
