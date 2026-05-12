# MindGuard 智能体 — 架构设计

## 项目概述

MindGuard 是一个基于 SpringAI 开发的心理健康聊天与监控智能助手平台。通过 LoRA 微调大模型优化心理状态识别能力，采用动态路由 RAG 对接管理员上传知识库实现针对性问答，集成 MCP 外部服务完成心理数据记录与异常预警。

## 技术选型

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.x |
| 微服务 | Spring Cloud | 2023.x |
| 微服务套件 | Spring Cloud Alibaba | 2023.x |
| 注册/配置 | Nacos | 2.3.x |
| 网关 | Spring Cloud Gateway | — |
| 熔断限流 | Sentinel | 1.8.x |
| 服务调用 | OpenFeign + LoadBalancer | — |
| ORM | MyBatis-Plus | 3.5.x |
| 数据库 | MySQL | 8.0 |
| AI 框架 | SpringAI | 1.0.x |
| 模型服务 | Ollama / OpenAI | — |
| 向量库 | Chroma | — |
| 微调 | LoRA | — |
| 外部集成 | MCP (Email / Excel) | — |
| 认证 | Spring Security + JWT | — |

## 服务架构

```
mindguard/
├── mindguard-gateway/          # 网关 (8080)
├── mindguard-auth/             # 认证授权 (8081)
├── mindguard-chat/             # 核心对话+心理分析 (8082)
├── mindguard-notification/     # 通知与数据导出 (8083)
├── mindguard-common/           # 共享模块
├── mindguard-api/              # Feign 接口定义
└── pom.xml
```

## 调用链路

```
客户端 → Gateway :8080 → Auth :8081
                       → Chat :8082
                            → Chroma (向量检索)
                            → Ollama/OpenAI (模型推理)
                            → LoRA 模型 (心理分析)
                            → Feign → Notification :8083
                                → MCP Email (预警)
                                → MCP Excel (导出)
```

## 各服务职责

### Gateway (8080)
- JWT 验签，无效 Token 返回 401
- Sentinel 限流（全局 100 QPS，登录 10 QPS）
- 路由规则转发到下游
- CORS 配置

### Auth (8081)
- 用户注册、登录、JWT 签发
- Spring Security 角色隔离（ADMIN / USER）
- 密码 BCrypt 加密存储
- 数据库: `mindguard_auth`，表: `user`

### Chat (8082)
- 多模型策略（Ollama / OpenAI），Flux 流式对话
- RAG 知识库：文档上传 → 分块 → Chroma 向量化 → 对话时检索 Top-K
- 心理状态分析：LoRA 模型推理 → 生成 PsychReport
- 高风险检测 → Feign 触发 Notification 报警
- 数据库: `mindguard_chat`，表: `conversation`, `knowledge_doc`, `psych_report`

### Notification (8083)
- MCP Email Tool：高风险预警邮件推送（送达率 ≥ 98%）
- MCP Excel Tool：心理数据导出为 Excel 文件
- 预警规则：连续 3 次 risk_level ≥ 3 触发邮件
- 数据库: `mindguard_notification`，表: `alert_rule`, `alert_log`

## 数据库设计

每个服务独享数据库，禁止跨服务直接访问数据库。

### Auth 库 — `mindguard_auth`

| 表 | 字段 |
|----|------|
| user | id, username, password(BCrypt), nickname, role(USER/ADMIN), status, created_at |

### Chat 库 — `mindguard_chat`

| 表 | 字段 |
|----|------|
| conversation | id, session_id, user_id, role(user/assistant), content, created_at |
| knowledge_doc | id, title, content, chunk_count, vector_ids, uploaded_by, created_at |
| psych_report | id, user_id, session_id, risk_level(0-4), analysis_text, keywords, created_at |

### Notification 库 — `mindguard_notification`

| 表 | 字段 |
|----|------|
| alert_rule | id, rule_name, risk_level_threshold, consecutive_count, enabled |
| alert_log | id, user_id, rule_id, alert_content, sent_to, sent_at, status |

## 安全设计

| 层级 | 措施 |
|------|------|
| Gateway | JWT 验签 |
| Auth | BCrypt 密码加密，登录接口 Sentienl 限流 |
| Chat | 知识库上传仅 ADMIN；对话仅登录用户 |
| Notification | 仅内部 Feign 调用，不对外暴露 |

## 统一响应格式

```json
{ "code": 200, "message": "success", "data": {} }
```

`mindguard-common` 提供 `Result<T>` 包装与 `GlobalExceptionHandler`。

## 本地开发环境

Docker Compose 管理外部依赖：MySQL、Nacos、Chroma、Ollama。
