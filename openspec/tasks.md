# MindGuard — Implementation Tasks

## Phase 1: Project Scaffold
- [ ] Initialize parent POM with Spring Boot 3.2.x + Spring Cloud 2023.x + Alibaba 2023.x
- [ ] Create mindguard-common module (Result<T>, GlobalExceptionHandler, JwtUtils)
- [ ] Create mindguard-api module (Feign interfaces)
- [ ] Set up Docker Compose file (MySQL, Nacos, Chroma, Ollama)

## Phase 2: Infrastructure
- [ ] mindguard-gateway: routing rules, JWT filter, Sentinel, CORS
- [ ] mindguard-auth: User entity, login/register, JWT token generation, Spring Security config
- [ ] SQL init scripts (3 databases)

## Phase 3: Core Chat
- [ ] mindguard-chat: conversation CRUD with MyBatis-Plus
- [ ] ModelStrategy interface + OllamaChatService + OpenAIChatService
- [ ] Flux streaming SSE endpoint (POST /api/chat/stream)
- [ ] RagService: document chunking, Chroma vector store integration
- [ ] Knowledge upload endpoint (ADMIN only)

## Phase 4: Psychology & Notification
- [ ] PsychAnalysisService: LoRA model inference, risk_level calculation
- [ ] PsychReport CRUD and query endpoints
- [ ] mindguard-notification: MCP Email tool integration
- [ ] AlertRule evaluation: consecutive high-risk detection
- [ ] MCP Excel export tool

## Phase 5: Integration & Polish
- [ ] OpenFeign integration: Chat → Notification alert calls
- [ ] Nacos service registration + config center for all services
- [ ] Sentinel dashboard setup and rules configuration
- [ ] End-to-end flow testing
