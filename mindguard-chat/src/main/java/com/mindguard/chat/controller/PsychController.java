package com.mindguard.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.chat.entity.PsychReport;
import com.mindguard.chat.mapper.PsychReportMapper;
import com.mindguard.common.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psych")
public class PsychController {

    private final PsychReportMapper psychReportMapper;

    public PsychController(PsychReportMapper psychReportMapper) {
        this.psychReportMapper = psychReportMapper;
    }

    @GetMapping("/report/{userId}")
    public Result<List<PsychReport>> getReports(@PathVariable Long userId) {
        List<PsychReport> reports = psychReportMapper.selectList(
                new LambdaQueryWrapper<PsychReport>()
                        .eq(PsychReport::getUserId, userId)
                        .orderByDesc(PsychReport::getCreatedAt));
        return Result.success(reports);
    }
}
