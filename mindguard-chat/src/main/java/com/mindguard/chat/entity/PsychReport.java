package com.mindguard.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("psych_report")
public class PsychReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String sessionId;
    private Integer riskLevel;
    private String analysisText;
    private String keywords;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
