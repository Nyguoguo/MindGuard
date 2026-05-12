package com.mindguard.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.notification.entity.AlertLog;
import com.mindguard.notification.entity.AlertRule;
import com.mindguard.notification.mapper.AlertLogMapper;
import com.mindguard.notification.mapper.AlertRuleMapper;
import com.mindguard.notification.mcp.EmailMcpTool;
import com.mindguard.notification.service.impl.AlertServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRuleMapper alertRuleMapper;
    @Mock
    private AlertLogMapper alertLogMapper;
    @Mock
    private EmailMcpTool emailMcpTool;

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertServiceImpl(alertRuleMapper, alertLogMapper, emailMcpTool);
    }

    @Test
    void shouldNotAlertWhenRiskLevelLow() {
        Map<String, Object> info = Map.of("userId", 1, "riskLevel", 2, "sessionId", "sess-1");
        alertService.handleAlert(info);
        verify(alertLogMapper, never()).insert(any());
    }

    @Test
    void shouldTriggerAlertWhenHighRisk() {
        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setRiskLevelThreshold(3);
        rule.setConsecutiveCount(3);
        rule.setEnabled(1);

        when(alertRuleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(rule));
        when(alertLogMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);
        when(alertLogMapper.insert(any(AlertLog.class))).thenReturn(1);
        doNothing().when(emailMcpTool).send(anyString(), anyString(), anyString());

        Map<String, Object> info = Map.of("userId", 1, "riskLevel", 3,
                "sessionId", "sess-1", "analysisText", "高风险检测");

        alertService.handleAlert(info);

        verify(alertLogMapper).insert(any(AlertLog.class));
        verify(emailMcpTool).send(contains("预警"), anyString(), anyString());
    }

    @Test
    void shouldSkipDisabledRule() {
        AlertRule rule = new AlertRule();
        rule.setId(1L);
        rule.setRiskLevelThreshold(3);
        rule.setConsecutiveCount(3);
        rule.setEnabled(0);

        when(alertRuleMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(rule));

        Map<String, Object> info = Map.of("userId", 1, "riskLevel", 4, "sessionId", "sess-1");

        alertService.handleAlert(info);
        verify(alertLogMapper, never()).insert(any());
    }
}
