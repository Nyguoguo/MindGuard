package com.mindguard.chat.service.impl;

import com.alibaba.fastjson2.JSON;
import com.mindguard.chat.entity.PsychReport;
import com.mindguard.chat.mapper.PsychReportMapper;
import com.mindguard.chat.service.PsychAnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class PsychAnalysisServiceImpl implements PsychAnalysisService {

    // High-risk keyword patterns (production would use LoRA model inference)
    private static final List<String> CRITICAL_PATTERNS = List.of(
            "自杀", "自伤", "不想活", "活着没意义", "结束生命", "自我了断",
            "想死", "活不下去", "没有希望", "绝望");

    private static final List<String> HIGH_RISK_PATTERNS = List.of(
            "抑郁", "焦虑症", "失眠", "崩溃", "每天都难受",
            "不想再继续", "很痛苦");

    private static final List<String> MODERATE_PATTERNS = List.of(
            "压力大", "焦虑", "紧张", "烦躁", "情绪低落",
            "睡不着", "吃不下");

    private final PsychReportMapper psychReportMapper;

    public PsychAnalysisServiceImpl(PsychReportMapper psychReportMapper) {
        this.psychReportMapper = psychReportMapper;
    }

    @Override
    public PsychReport analyze(String sessionId, Long userId, String text) {
        int riskLevel = calculateRiskLevel(text);
        List<String> keywords = extractKeywords(text);

        PsychReport report = new PsychReport();
        report.setUserId(userId);
        report.setSessionId(sessionId);
        report.setRiskLevel(riskLevel);
        report.setAnalysisText(buildAnalysis(riskLevel, keywords));
        report.setKeywords(JSON.toJSONString(keywords));

        psychReportMapper.insert(report);
        return report;
    }

    private int calculateRiskLevel(String text) {
        String lower = text.toLowerCase();

        for (String pattern : CRITICAL_PATTERNS) {
            if (lower.contains(pattern)) return 4;
        }
        for (String pattern : HIGH_RISK_PATTERNS) {
            if (lower.contains(pattern)) return 3;
        }
        for (String pattern : MODERATE_PATTERNS) {
            if (lower.contains(pattern)) return 2;
        }

        return text.length() > 50 ? 1 : 0;
    }

    private List<String> extractKeywords(String text) {
        String lower = text.toLowerCase();
        return List.of("压力", "焦虑", "抑郁", "失眠", "情绪").stream()
                .filter(lower::contains)
                .toList();
    }

    private String buildAnalysis(int riskLevel, List<String> keywords) {
        switch (riskLevel) {
            case 0:
            case 1:
                return "心理状态正常，未检测到明显风险信号";
            case 2:
                return "检测到中等心理压力信号: " + String.join(", ", keywords);
            case 3:
                return "检测到高风险心理信号，建议关注: " + String.join(", ", keywords);
            case 4:
                return "检测到危险心理信号，建议立即介入干预: " + String.join(", ", keywords);
            default:
                return "分析完成";
        }
    }
}
