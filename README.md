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

## 当前进度（第三天）

已完成：

- Spring Boot项目初始化
- RestClient配置
- Hacker News热门资讯采集
- 使用 Rome 解析 RSS/Atom，采集 Spring 官方博客资讯
- Java对象与JSON数据转换
- 合并 Hacker News 和 RSS 采集结果
- 过滤空文章、空标题和空链接
- 去除标题和 URL 两端的空白
- 按规范化后的 URL 去重，保留首次出现的文章及原有顺序
- 为文章标记来源：Hacker News 为 `hn`，RSS 为 `rss`
- 将清洗后的多来源资讯保存到 `data/articles.json`
- Hacker News 单篇资讯请求异常处理
- 编写三个清洗逻辑单元测试：无效数据过滤、规范化与去重、空列表处理

待完成：

- GitHub热门项目采集
- ArXiv资讯采集
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
- Rome 2.1.0（RSS/Atom 解析）
- JUnit Jupiter（单元测试）

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
│  ├─ HackerNewsCollector.java
│  └─ RssCollector.java
├─ config/
│  └─ RestClientConfig.java
├─ model/
│  └─ Article.java
├─ runner/
│  └─ CollectionRunner.java
└─ service/
   └─ ArticleService.java

src/test/java/com/example/aidaily/
├─ AiDailyApplicationTests.java
└─ service/
   └─ ArticleServiceTest.java
```

## 当前处理流程

```text
Hacker News + RSS → 合并 → 过滤无效文章 → 规范化字段 → 按 URL 去重 → 保存 JSON
```

- 采集器负责获取文章并补充来源信息。
- `ArticleService` 负责过滤、规范化和去重。
- `CollectionRunner` 负责组织流程并写入文件。

当前去重依据是去除两端空白后的 URL 完全相同，不合并查询参数或路径不同的链接。两个来源出现相同 URL 时，保留先加入列表的文章；当前 Hacker News 在前，RSS 在后。

## 运行项目

需要 Java 17，并能访问 Maven 依赖仓库和资讯接口。在项目根目录的 PowerShell 中运行：

```powershell
.\mvnw.cmd spring-boot:run
```

也可以在 IDE 中运行 `AiDailyApplication`。应用启动后执行一次采集，最多请求前 10 条 Hacker News 热门文章和前 5 条 RSS 文章，清洗后覆盖保存到工作目录下的 `data/articles.json`。最终数量取决于源数据、请求结果和清洗情况，不保证为 15 条。

## 运行清洗单元测试

```powershell
.\mvnw.cmd "-Dtest=ArticleServiceTest" test
```

三个测试验证：

- 过滤空文章、空标题及空链接。
- 去除标题和 URL 两端空白，按 URL 保留第一条文章并保持顺序及其他字段。
- 空列表输入返回空列表。

这些测试直接调用 `ArticleService`，不启动 Spring、不请求资讯接口，也不改写 JSON 文件。预期结果为 3 个测试通过，失败和错误均为 0。

`AiDailyApplicationTests` 是另一项 Spring 上下文测试，会触发启动采集并依赖外部网络，不包含在上述命令中。
