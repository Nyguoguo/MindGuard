package com.mindguard.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("conversation")
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sessionId;
    private Long userId;
    private String role;
    private String content;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
