package com.mindguard.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_doc")
public class KnowledgeDoc {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Integer chunkCount;
    private String vectorIds;
    private Long uploadedBy;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
