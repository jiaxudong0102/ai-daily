# AI Daily

AI Daily 是一个基于 Spring Boot 开发的个性化 AI 资讯日报系统。

项目将从 Hacker News、GitHub、ArXiv 和 RSS 等信息源采集技术资讯，并通过大模型完成中文摘要、内容分类、价值分析和个性化推荐，最终生成每日资讯页面并推送到飞书。

## 项目目标

项目计划实现以下功能：

- 多平台技术资讯采集
- 文章清洗与去重
- AI中文摘要与分类
- 用户兴趣管理
- 个性化资讯推荐
- 每日资讯页面生成
- 飞书消息推送
- 定时任务自动执行
- Docker部署

## 当前进度

已完成：

- Spring Boot项目初始化
- RestClient配置
- Hacker News热门资讯采集
- Java对象与JSON数据转换
- 将10条资讯保存到本地JSON文件
- 单篇资讯采集异常处理

待完成：

- RSS资讯采集
- GitHub热门项目采集
- 数据清洗与去重
- 数据库存储
- 大模型摘要
- 推荐排序
- 日报页面
- 飞书推送
- 定时任务
- Docker部署

## 技术栈

- Java 17
- Spring Boot 4.0.9
- Spring Web MVC
- RestClient
- Jackson
- Maven
- Hacker News API

后续计划引入：

- MySQL
- Spring Data JPA 或 MyBatis-Plus
- Spring AI
- Thymeleaf
- Docker

## 项目结构

```text
src/main/java/com/example/aidaily/
├─ AiDailyApplication.java
├─ collecter/
│  └─ HackerNewsCollector.java
├─ config/
│  └─ RestClientConfig.java
├─ model/
│  └─ Article.java
└─ runner/
   └─ CollectionRunner.java