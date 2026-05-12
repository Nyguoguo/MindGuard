package com.mindguard.chat.service;

import com.mindguard.chat.entity.PsychReport;
import com.mindguard.chat.mapper.PsychReportMapper;
import com.mindguard.chat.service.impl.PsychAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PsychAnalysisServiceTest {

    @Mock
    private PsychReportMapper psychReportMapper;

    private PsychAnalysisService psychAnalysisService;

    @BeforeEach
    void setUp() {
        psychAnalysisService = new PsychAnalysisServiceImpl(psychReportMapper);
    }

    @Test
    void shouldDetectNormalState() {
        when(psychReportMapper.insert(any(PsychReport.class))).thenReturn(1);

        PsychReport report = psychAnalysisService.analyze("session-1", 1L,
                "今天天气真好，心情不错");
        assertNotNull(report);
        assertTrue(report.getRiskLevel() <= 1);
        assertEquals("session-1", report.getSessionId());
    }

    @Test
    void shouldDetectHighRisk() {
        when(psychReportMapper.insert(any(PsychReport.class))).thenReturn(1);

        PsychReport report = psychAnalysisService.analyze("session-2", 1L,
                "我觉得活着没有意义，每天都很难受，不想再继续了");
        assertNotNull(report);
        assertTrue(report.getRiskLevel() >= 3,
                "Expected risk_level >= 3 but got " + report.getRiskLevel());
    }

    @Test
    void shouldDetectModerateRisk() {
        when(psychReportMapper.insert(any(PsychReport.class))).thenReturn(1);

        PsychReport report = psychAnalysisService.analyze("session-3", 1L,
                "最近失眠严重，经常莫名焦虑，学习也跟不上");
        assertNotNull(report);
        assertTrue(report.getRiskLevel() >= 2);
    }
}
