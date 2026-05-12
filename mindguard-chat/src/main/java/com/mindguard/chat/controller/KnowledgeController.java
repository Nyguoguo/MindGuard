package com.mindguard.chat.controller;

import com.mindguard.chat.service.RagService;
import com.mindguard.common.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final RagService ragService;

    public KnowledgeController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/upload")
    public Result<Void> upload(@RequestBody Map<String, String> body,
                               @RequestHeader("X-User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("仅管理员可上传知识库文档");
        }
        ragService.storeDocument(body.get("title"), body.get("content"));
        return Result.success();
    }
}
