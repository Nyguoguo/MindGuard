package com.mindguard.notification.controller;

import com.mindguard.notification.service.AlertService;
import com.mindguard.common.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/alert")
    public Result<Void> alert(@RequestBody Map<String, Object> alertInfo) {
        alertService.handleAlert(alertInfo);
        return Result.success();
    }
}
