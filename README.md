# MindGuard 智能体

基于 SpringAI 开发聊天与心理监控助手，通过 LoRA 微调大模型优化心理状态识别能力，采用动态路由 RAG 对接管理员上传知识库实现针对性问答，集成 MCP 外部服务完成心理数据记录与异常预警。系统采用 Spring Cloud Alibaba 微服务架构，基于 Nacos 服务治理 + Gateway 统一网关 + Sentinel 熔断限流 + OpenFeign 服务调用，拆分为认证授权、核心对话与心理分析、MCP 通知与数据导出四大微服务，实现高可用分布式部署。

## 项目简介

基于 SpringAI + Spring Cloud Alibaba 微服务架构，集成 Ollama/OpenAI 多模型流式对话、Chroma 向量数据库 + RAG 动态知识库、LoRA 心理状态识别、MCP 邮件预警与 Excel 数据导出，构建面向高校场景的心理健康智能监控平台。

### 核心能力

- **多模型流式对话**：基于 Flux 模式的 SSE 流式会话，支持 Ollama / OpenAI 双模型策略切换
- **RAG 知识库**：管理员上传心理咨询文档，Chromat 向量化 + 动态路由检索，对话时自动增强回复
- **心理状态识别**：LoRA 微调模型分析对话文本，输出 0-4 级心理风险评估报告
- **高风险预警**：连续识别高风险 → MCP 邮件自动推送至相关负责人，送达率 ≥ 98%
- **数据可视化**：MCP Excel 工具自动导出心理数据报表，支持统计分析
- **权限隔离**：Spring Security 管理员/用户角色严格隔离，知识库上传仅管理员

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.5 |
| 微服务 | Spring Cloud | 2023.0.2 |
| 注册/配置 | Nacos | 2.3.0 |
| 网关 | Spring Cloud Gateway | — |
| 熔断限流 | Sentinel | 1.8 |
| 服务调用 | OpenFeign + LoadBalancer | — |
| 认证 | Spring Security + JWT | 0.12.5 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 容器化 | Docker Compose | — |
| AI 框架 | SpringAI | 1.0.0-M4 |
| 模型推理 | Ollama | latest |
| 向量数据库 | Chroma | latest |
| 模型微调 | LoRA | — |
| 电子邮件 | Spring Mail | — |
| Excel | Apache POI | 5.2.5 |

## 项目结构

```
mindguard/
├── mindguard-gateway/          # 网关服务 (8080)
├── mindguard-auth/             # 认证授权服务 (8081)
├── mindguard-chat/             # 核心对话+心理分析服务 (8082)
├── mindguard-notification/     # 通知与数据导出服务 (8083)
├── mindguard-common/           # 共享模块
├── mindguard-api/              # Feign 接口模块
├── docker-compose.yml           # 本地基础设施
├── sql/init.sql                 # 数据库初始化脚本
└── docs/superpowers/            # 设计文档与实施计划
```

## 快速启动

### 1. 启动基础设施

```bash
docker-compose up -d
```

启动 MySQL (3306)、Nacos (8848)、Chroma (8000)、Ollama (11434)。

### 2. 拉取大模型（首次）

```bash
docker exec -it mindguard-ollama ollama pull qwen2.5:7b
```

### 3. 配置邮件通知

修改 `mindguard-notification/src/main/resources/application.yml` 中的 `spring.mail.*` 配置：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-auth-code
```

### 4. 启动微服务（按顺序）

```bash
# 1. 认证服务
cd mindguard-auth && mvn spring-boot:run

# 2. 对话服务
cd mindguard-chat && mvn spring-boot:run

# 3. 通知服务
cd mindguard-notification && mvn spring-boot:run

# 4. 网关服务
cd mindguard-gateway && mvn spring-boot:run
```

### 5. 验证

```bash
# 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"student01","password":"pass123","nickname":"小明"}'

# 登录获取Token
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"student01","password":"pass123"}'

# 流式对话
curl -X POST http://localhost:8080/api/chat/stream \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"message":"最近压力很大，睡不着觉"}'
```

## 微服务架构图

```
用户/前端
    │
    ▼
┌─────────────────────┐
│  Gateway :8080      │  JWT验签 + Sentinel限流
└──┬──────┬──────┬────┘
   │      │      │
   ▼      ▼      ▼
 Auth   Chat   Notification
:8081   :8082   :8083
   │      │      │
   ▼      ▼      ▼
 MySQL  MySQL  MySQL
        Chroma
        Ollama
```

## 数据库

| 服务 | 数据库 | 核心表 |
|------|--------|--------|
| Auth | mindguard_auth | user |
| Chat | mindguard_chat | conversation, knowledge_doc, psych_report |
| Notification | mindguard_notification | alert_rule, alert_log |

## 预警规则

- 心理风险等级：0=正常, 1=轻度, 2=中度, 3=高风险, 4=危险
- 连续 3 次识别为 ≥ 3 级 → 自动触发邮件预警
- 邮件内容包含：学生ID、风险等级、分析结果、会话记录
