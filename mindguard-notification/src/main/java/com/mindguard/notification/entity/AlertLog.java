package com.mindguard.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("alert_log")
public class AlertLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long ruleId;
    private String alertContent;
    private String sentTo;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime sentAt;
    private String status;
}
