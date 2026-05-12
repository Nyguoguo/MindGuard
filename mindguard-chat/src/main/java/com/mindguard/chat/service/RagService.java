package com.mindguard.chat.service;

public interface RagService {
    String retrieve(String query);
    void storeDocument(String title, String content);
}
