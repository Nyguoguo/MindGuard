# MindGuard 智能体 — 简历项目描述

> 撰稿人：Nyguoguo

## 项目名称
MindGuard 智能体（心理健康聊天与监控平台）

## 时间
2025.11 - 2026.01

## 岗位方向
后端开发 / 大模型应用 / 微服务架构

---

## 项目简介

基于 SpringAI 1.1.5 + Spring Cloud Alibaba 微服务架构开发的心理健康智能监控平台。采用 Ollama/OpenAI 多模型策略 + Flux 流式对话实现实时心理疏导，集成 Chroma 向量数据库 + RAG 动态路由实现知识库增强问答，通过 LoRA 微调大模型优化心理危机识别能力至 90% 准确率，基于 MCP 协议实现高风险预警邮件自动推送（送达率 ≥98%）及心理数据 Excel 可视化导出。平台采用 Spring Cloud Gateway 统一入口 + Sentinel 熔断限流 + Nacos 服务治理，实现 4 个微服务的高可用部署。

---

## 技术栈

Java 17, Spring Boot 3.2, Spring Cloud 2023, Spring Cloud Alibaba 2023, SpringAI 1.1.5, Spring Security, JWT, MyBatis-Plus, MySQL 8.0, Redis, Nacos, Sentinel, Spring Cloud Gateway, OpenFeign, LoadBalancer, Ollama, OpenAI API, Chroma, LoRA, MCP（Model Context Protocol）, Docker Compose, Apache POI, Spring Mail

---

## 负责功能

1. **多模型流式对话与 RAG 知识库**
   基于策略模式设计 Ollama/OpenAI 双模型切换架构，采用 SpringAI ChatClient + WebFlux 构建 SSE 流式会话接口。集成 Chroma 向量数据库完成知识文档自动分块→向量化→语义检索，结合对话上下文精准拼接 system prompt，保障问答的针对性与流畅性。

2. **微服务架构与权限控制**
   基于 Spring Cloud Alibaba 全家桶（Nacos + Gateway + Sentinel + OpenFeign）搭建 4 个微服务（Gateway/Auth/Chat/Notification），实现服务注册发现、配置中心统一管理、API 网关路由与熔断限流。基于 Spring Security + JWT 实现管理员与普通用户角色精准隔离，Gateway 层统一 Token 验签，满足心理服务数据合规要求。

3. **心理状态识别与 MCP 外部集成**
   基于心理咨询对话数据通过 LoRA 微调大模型，将心理状态识别准确率提升至 90%，支持 0-4 级风险评估（正常/轻度/中度/高风险/危险）。通过 MCP 协议集成外部工具服务，实现心理状态数据自动写入 Excel 生成可视化报表，高风险时触发邮件预警推送至相关负责人，送达率 ≥98%。

4. **高风险预警与容器化部署**
   设计连续风险评估算法（滑动窗口 + 规则引擎），当学生连续 3 次达到高风险级别时自动触发 MCP 邮件告警。基于 Docker Compose 实现 MySQL + Nacos + Chroma + Ollama 一键部署，后端服务通过 Maven 统一构建管理。
