package com.mindguard.chat.controller;

import com.mindguard.chat.service.ChatService;
import com.mindguard.common.dto.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody Map<String, String> body,
                                   @RequestHeader("X-User-Id") Long userId) {
        String sessionId = body.getOrDefault("sessionId", "sess-" + userId + "-" + System.currentTimeMillis());
        return chatService.streamChat(sessionId, userId, body.get("message"));
    }

    @GetMapping("/history")
    public Result<Object> getHistory(@RequestParam("sessionId") String sessionId) {
        return Result.success(chatService.getHistory(sessionId));
    }
}
