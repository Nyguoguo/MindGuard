# MindGuard — Implementation Plan

## Phase 1: Project Scaffold (基础骨架)

### 1.1 父 POM (`pom.xml`)
- Spring Boot 3.2.x + Spring Cloud 2023.0.x + Spring Cloud Alibaba 2023.0.x
- 模块声明: gateway, auth, chat, notification, common, api
- 公共依赖管理: Lombok, MyBatis-Plus 3.5.5, JWT (jjwt 0.12), fastjson2

### 1.2 Docker Compose (`docker-compose.yml`)
- MySQL 8.0 (3306), Nacos standalone (8848/9848), Chroma (8000), Ollama (11434)

### 1.3 SQL 初始化 (`sql/init.sql`)
- 创建 3 个数据库 + 所有表

### 1.4 mindguard-common 模块
- `Result<T>` 统一响应体
- `GlobalExceptionHandler` (ControllerAdvice)
- `JwtUtils` (生成/解析 Token)

### 1.5 mindguard-api 模块
- `AuthFeign` — GET /api/auth/me
- `NotificationFeign` — POST /api/notify/alert (内部调用)

---

## Phase 2: Auth 服务 (8081)

TDD order:
1. **Test** → `UserMapper` CRUD + `AuthService` login/register
2. **Impl** → User entity, UserMapper, AuthService, AuthController
3. **Impl** → Spring Security + JWT filter
4. **Impl** → AuthApplication + application.yml (Nacos注册)

Files:
```
mindguard-auth/src/main/java/com/mindguard/auth/
├── AuthApplication.java
├── config/SecurityConfig.java
├── controller/AuthController.java
├── entity/User.java
├── mapper/UserMapper.java
├── service/AuthService.java
├── service/impl/AuthServiceImpl.java
└── resources/application.yml
mindguard-auth/src/test/java/com/mindguard/auth/
├── service/AuthServiceTest.java
└── controller/AuthControllerTest.java
```

---

## Phase 3: Gateway (8080)

TDD order:
1. **Test** → Gateway routes + JWT filter with mock
2. **Impl** → Route config (YAML), JwtAuthFilter, CorsConfig, Sentinel config

Files:
```
mindguard-gateway/src/main/java/com/mindguard/gateway/
├── GatewayApplication.java
├── config/GatewayConfig.java
├── config/CorsConfig.java
└── filter/JwtAuthFilter.java
mindguard-gateway/src/main/resources/application.yml
```

---

## Phase 4: Chat 服务 (8082)

TDD order:
1. **Test** → ConversationMapper, RagService, ModelStrategy
2. **Impl** → Conversation entity + mapper
3. **Impl** → ModelStrategy interface + OllamaChatService (Flux SSE)
4. **Impl** → RagService (Chroma vector store + document chunking)
5. **Impl** → ChatController (stream endpoint)
6. **Impl** → PsychAnalysisService + PsychReport entity + mapper
7. **Impl** → Knowledge upload endpoint (admin only)

Files:
```
mindguard-chat/src/main/java/com/mindguard/chat/
├── ChatApplication.java
├── config/OllamaConfig.java
├── config/ChromaConfig.java
├── controller/ChatController.java
├── controller/KnowledgeController.java
├── controller/PsychController.java
├── entity/Conversation.java
├── entity/KnowledgeDoc.java
├── entity/PsychReport.java
├── mapper/ConversationMapper.java
├── mapper/KnowledgeDocMapper.java
├── mapper/PsychReportMapper.java
├── model/ModelStrategy.java
├── model/impl/OllamaChatService.java
├── model/impl/OpenAIChatService.java
├── service/ChatService.java
├── service/RagService.java
├── service/PsychAnalysisService.java
├── service/impl/...
└── resources/application.yml
mindguard-chat/src/test/java/com/mindguard/chat/
├── service/ChatServiceTest.java
├── service/RagServiceTest.java
└── service/PsychAnalysisServiceTest.java
```

---

## Phase 5: Notification 服务 (8083)

TDD order:
1. **Test** → AlertService, EmailMcpTool
2. **Impl** → AlertRule + AlertLog entity + mapper
3. **Impl** → AlertService (evaluate consecutive risk_level)
4. **Impl** → MCP Email tool (extract email via SMTP)
5. **Impl** → MCP Excel tool (Apache POI)

Files:
```text
mindguard-notification/src/main/java/com/mindguard/notification/
├── NotificationApplication.java
├── config/McpConfig.java
├── config/MailConfig.java
├── controller/AlertController.java (内部Feign接口)
├── entity/AlertRule.java
├── entity/AlertLog.java
├── mapper/AlertRuleMapper.java
├── mapper/AlertLogMapper.java
├── mcp/EmailMcpTool.java
├── mcp/ExcelMcpTool.java
├── service/AlertService.java
├── service/ExcelService.java
├── service/impl/...
└── resources/application.yml
mindguard-notification/src/test/java/com/mindguard/notification/
├── service/AlertServiceTest.java
└── mcp/EmailMcpToolTest.java
```

---

## Phase 6: Integration & Verification

- [ ] Nacos 注册：启动 4 个服务，确认都在 Nacos 注册成功
- [ ] OpenFeign 集成：Chat 识别高风险 → Feign → Notification 发邮件
- [ ] Sentinel 规则：Gateway 登录接口 QPS 限流 10
- [ ] 端到端测试：登录 → 对话 → 心理分析 → 预警 → 邮件通知
