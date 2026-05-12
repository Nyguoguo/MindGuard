package com.mindguard.api.feign;

import com.mindguard.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "mindguard-notification")
public interface NotificationFeign {

    @PostMapping("/api/notify/alert")
    Result<Void> sendAlert(@RequestBody Map<String, Object> alertInfo);
}
