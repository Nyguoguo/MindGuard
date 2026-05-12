# MindGuard — Feature Specifications

## Feature: User Authentication

### Scenario: User Login
- **Given** a registered user with username "student01" and password "pass123"
- **When** POST /api/auth/login with valid credentials
- **Then** returns JWT token with role "USER" and 200 status

### Scenario: Login Rate Limiting
- **Given** Gateway Sentinel configured with login QPS limit 10
- **When** 15 login requests arrive within 1 second
- **Then** first 10 requests succeed, remaining 5 return 429 Too Many Requests

### Scenario: Invalid Token
- **Given** an expired or tampered JWT token
- **When** any authenticated API is called
- **Then** Gateway returns 401 Unauthorized

### Scenario: Role Isolation
- **Given** a USER tries to upload knowledge document
- **When** POST /api/knowledge/upload is called
- **Then** Chat service returns 403 Forbidden

---

## Feature: Streaming Chat

### Scenario: Normal Chat
- **Given** authenticated user with valid JWT
- **When** POST /api/chat/stream with message "最近压力很大"
- **Then** server returns SSE stream with AI response chunks
- **And** conversation is persisted with session_id

### Scenario: RAG Enhancement
- **Given** knowledge base contains document about "考试焦虑应对方法"
- **When** user asks "考试前很紧张怎么办"
- **Then** AI response references knowledge base content
- **And** Chroma retrieves relevant chunks with similarity > 0.7

### Scenario: Multi-Model Switch
- **Given** config `model.strategy=ollama`
- **When** chat is initiated
- **Then** Ollama model is used for inference
- **Given** config `model.strategy=openai`
- **When** chat is initiated
- **Then** OpenAI API is used for inference

---

## Feature: Psychological Analysis

### Scenario: Risk Detection
- **Given** user conversation text contains depression indicators
- **When** PsychAnalysisService analyzes the text
- **Then** psych_report with risk_level >= 3 is generated

### Scenario: High-Risk Alert
- **Given** user has 3 consecutive psych_reports with risk_level >= 3
- **When** the 3rd report is saved
- **Then** Notification service is triggered via Feign
- **And** email alert is sent to configured admin emails
- **And** alert_log is recorded with status "sent"

### Scenario: Normal State
- **Given** user conversation text is neutral/positive
- **When** PsychAnalysisService analyzes the text
- **Then** psych_report with risk_level 0 or 1 is generated

---

## Feature: Knowledge Base Management

### Scenario: Upload Document
- **Given** admin user uploads a document "心理危机干预指南.md"
- **When** POST /api/knowledge/upload
- **Then** document is chunked and vectorized
- **And** vectors are stored in Chroma
- **And** knowledge_doc record is created

### Scenario: Non-Admin Cannot Upload
- **Given** normal user tries to upload a document
- **When** POST /api/knowledge/upload
- **Then** returns 403 Forbidden

---

## Feature: Data Export

### Scenario: Export Psych Reports
- **Given** admin requests export of user's psych reports
- **When** GET /api/psych/report/{userId}?format=excel
- **Then** an Excel file is generated via MCP
- **And** file contains columns: date, risk_level, analysis_text, keywords

## Edge Cases & Error Handling

- Chroma unavailable → chat falls back to model-only (no RAG), log warning
- Ollama timeout (>30s) → return 504 with "模型响应超时，请重试"
- Excel export with 100K+ rows → paginate queries, MCP streams chunked output
- Concurrent sessions per user → maintain separate session_id
