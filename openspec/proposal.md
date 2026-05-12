# Proposal: MindGuard 智能体

## Summary

构建基于 SpringAI + Spring Cloud 的心理健康聊天与监控平台，支持多模型对话、RAG 知识库、LoRA 心理状态识别和 MCP 外部服务集成。

## Motivation

当前缺乏面向高校心理健康场景的智能 Agent 系统。MindGuard 通过大模型对话 + 心理状态监控，帮助辅导员及早发现高风险学生并触发预警。

## User Stories

1. **学生对话** — 学生通过流式聊天获取心理支持，回复结合专业知识库增强
2. **心理状态识别** — 系统自动分析对话文本，评估心理危机等级（0-4级）
3. **管理员知识库** — 管理员上传心理咨询文档，系统自动构建向量库
4. **高风险预警** — 识别到高风险时自动发送邮件通知相关责任人
5. **数据导出** — 心理咨询数据支持 Excel 导出，便于统计分析

## Affected Modules

- mindguard-gateway — API 网关
- mindguard-auth — 认证授权服务
- mindguard-chat — 核心对话与心理分析服务
- mindguard-notification — 通知与数据导出服务
- mindguard-common — 共享模块
- mindguard-api — Feign 接口模块

## Acceptance Criteria

- 学生可发起流式对话并获取 RAG 增强回复
- 心理状态识别准确率 ≥ 90%（LoRA 微调后）
- 高风险预警邮件送达率 ≥ 98%
- 管理员与普通用户权限严格隔离
- 各服务启动即注册 Nacos，配置统一管理

## Risks

- LoRA 微调需依赖 GPU 资源进行训练
- Chroma 向量库与 MySQL 需保证数据一致性
- 流式对话在网络不稳定时需处理断连重试
