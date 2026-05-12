package com.mindguard.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.notification.entity.AlertLog;
import com.mindguard.notification.entity.AlertRule;
import com.mindguard.notification.mapper.AlertLogMapper;
import com.mindguard.notification.mapper.AlertRuleMapper;
import com.mindguard.notification.mcp.EmailMcpTool;
import com.mindguard.notification.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRuleMapper alertRuleMapper;
    private final AlertLogMapper alertLogMapper;
    private final EmailMcpTool emailMcpTool;

    public AlertServiceImpl(AlertRuleMapper alertRuleMapper,
                            AlertLogMapper alertLogMapper,
                            EmailMcpTool emailMcpTool) {
        this.alertRuleMapper = alertRuleMapper;
        this.alertLogMapper = alertLogMapper;
        this.emailMcpTool = emailMcpTool;
    }

    @Override
    public void handleAlert(Map<String, Object> alertInfo) {
        Integer riskLevel = (Integer) alertInfo.get("riskLevel");
        Long userId = Long.valueOf(alertInfo.get("userId").toString());

        List<AlertRule> rules = alertRuleMapper.selectList(
                new LambdaQueryWrapper<AlertRule>().eq(AlertRule::getEnabled, 1));

        for (AlertRule rule : rules) {
            if (riskLevel < rule.getRiskLevelThreshold()) continue;

            Long consecutiveCount = alertLogMapper.selectCount(
                    new LambdaQueryWrapper<AlertLog>()
                            .eq(AlertLog::getUserId, userId)
                            .orderByDesc(AlertLog::getSentAt)
                            .last("LIMIT " + rule.getConsecutiveCount()));

            if (consecutiveCount >= rule.getConsecutiveCount()) {
                sendAlert(userId, rule, alertInfo);
            }
        }
    }

    private void sendAlert(Long userId, AlertRule rule, Map<String, Object> alertInfo) {
        String subject = "MindGuard - 心理高风险预警";
        String body = String.format("""
                预警类型: %s
                学生ID: %d
                风险等级: %s
                分析结果: %s
                请及时关注并采取干预措施。
                """,
                rule.getRuleName(), userId,
                alertInfo.get("riskLevel"),
                alertInfo.getOrDefault("analysisText", "无"));

        String adminEmail = "admin@mindguard.edu";
        emailMcpTool.send(subject, body, adminEmail);

        AlertLog entry = new AlertLog();
        entry.setUserId(userId);
        entry.setRuleId(rule.getId());
        entry.setAlertContent(body);
        entry.setSentTo(adminEmail);
        entry.setStatus("sent");
        alertLogMapper.insert(entry);

        log.info("Alert sent for user {} at risk level {}", userId, alertInfo.get("riskLevel"));
    }
}
