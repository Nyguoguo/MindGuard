package com.mindguard.chat.model;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface ModelStrategy {
    Flux<String> chat(List<Map<String, String>> messages);
}
