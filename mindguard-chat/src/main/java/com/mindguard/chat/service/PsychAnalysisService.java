package com.mindguard.chat.service;

import com.mindguard.chat.entity.PsychReport;

public interface PsychAnalysisService {
    PsychReport analyze(String sessionId, Long userId, String text);
}
