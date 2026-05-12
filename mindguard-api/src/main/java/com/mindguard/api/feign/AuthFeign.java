package com.mindguard.api.feign;

import com.mindguard.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "mindguard-auth")
public interface AuthFeign {

    @GetMapping("/api/auth/me/internal")
    Result<Map<String, Object>> getUserInfo(@RequestParam("userId") Long userId);
}
