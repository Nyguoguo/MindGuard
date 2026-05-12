package com.mindguard.notification.service;

import java.util.Map;

public interface AlertService {
    void handleAlert(Map<String, Object> alertInfo);
}
