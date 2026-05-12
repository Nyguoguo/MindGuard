# MindGuard — Technical Design

## Architecture Overview

```
User / Frontend
    │
    ▼
┌─────────────────────┐
│  Gateway :8080      │  JWT + Sentinel + Routes
└──┬──────┬──────┬────┘
   │      │      │
   ▼      ▼      ▼
 Auth  Chat  Notification
 :8081  :8082  :8083
   │      │      │
   ▼      ▼      ▼
 MySQL  MySQL  MySQL
        Chroma
        Ollama
```

## Technology Stack

- Java 17, Spring Boot 3.2.x, Spring Cloud 2023.x
- Spring Cloud Alibaba 2023.x (Nacos 2.3, Sentinel 1.8)
- Spring Cloud Gateway, OpenFeign, LoadBalancer
- Spring Security + JWT
- MyBatis-Plus 3.5.x, MySQL 8.0
- SpringAI 1.0.x, Chroma, Ollama/OpenAI

## Database Design

Each service owns its database. Cross-service data access via Feign only.

### mindguard_auth
- `user` — id(PK), username, password(BCrypt), nickname, role(USER|ADMIN), status(TINYINT), created_at

### mindguard_chat
- `conversation` — id(PK), session_id, user_id, role(user|assistant), content, created_at
- `knowledge_doc` — id(PK), title, content, chunk_count, vector_ids(JSON), uploaded_by, created_at
- `psych_report` — id(PK), user_id, session_id, risk_level(0-4), analysis_text, keywords(JSON), created_at

### mindguard_notification
- `alert_rule` — id(PK), rule_name, risk_level_threshold, consecutive_count, enabled
- `alert_log` — id(PK), user_id, rule_id, alert_content, sent_to, sent_at, status

## Key API Endpoints

| Method | Path | Service | Auth |
|--------|------|---------|------|
| POST | /api/auth/login | Auth | Public |
| POST | /api/auth/register | Auth | Public |
| GET | /api/auth/me | Auth | Auth |
| POST | /api/chat/stream | Chat | Auth |
| POST | /api/knowledge/upload | Chat | ADMIN |
| GET | /api/psych/report/{userId} | Chat | Auth |
| GET | /api/chat/history | Chat | Auth |
| POST | /api/notify/alert (internal) | Notification | Feign only |

## Security

- Gateway: JWT validation, rate limiting
- Auth: BCrypt, login 10 QPS cap
- Chat: Role-based access (ADMIN for knowledge upload)
- Notification: Internal-only via Feign

## Data Flow: Chat with RAG

1. User sends message → Gateway validates JWT → Chat receives
2. `RagService` queries Chroma for Top-K chunks
3. `ModelStrategy` builds prompt: system + chunks + history + user message
4. Stream `Flux<String>` via SSE back to user
5. `PsychAnalysisService` runs async analysis
6. If `risk_level >= 3` for 3 consecutive sessions → Feign `Notification.alert()`

## Deployment

Local dev: Docker Compose for MySQL, Nacos, Chroma, Ollama
Each service runs as a standalone Spring Boot app.
