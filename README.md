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

## 当前进度（第四天）

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
- 接入 MySQL 和 Spring Data JPA，使用 `ArticleEntity` 映射 `articles` 表
- 将清洗后的文章保存到数据库，并按 URL 检查已有记录
- 通过 Repository 查询最近入库的 20 篇文章
- 从配置文件读取 Hacker News 和 RSS 的采集数量

待完成：

- GitHub热门项目采集
- ArXiv资讯采集
- 在实际数据库中验收唯一索引、重启保留与固定 URL 重复采集
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
- MySQL 8.0
- Spring Data JPA

后续计划引入：

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
├─ entity/
│  └─ ArticleEntity.java
├─ model/
│  └─ Article.java
├─ repository/
│  └─ ArticleRepository.java
├─ runner/
│  └─ CollectionRunner.java
└─ service/
   ├─ ArticleService.java
   └─ ArticlePersistenceService.java

src/test/java/com/example/aidaily/
├─ AiDailyApplicationTests.java
└─ service/
   └─ ArticleServiceTest.java
```

## 当前处理流程

```text
Hacker News + RSS → 合并 → 清洗并按 URL 去重 → 查询数据库已有 URL → 保存新文章到 MySQL → 保存本次采集 JSON
```

- 采集器负责获取文章并补充来源信息。
- `ArticleService` 负责过滤、规范化和去重。
- `CollectionRunner` 负责组织流程、入库和写入文件。
- `ArticlePersistenceService` 将采集模型转换为 JPA 实体，`ArticleRepository` 负责保存和查询。

当前去重依据是去除两端空白后的 URL 完全相同，不合并查询参数或路径不同的链接。两个来源出现相同 URL 时，保留先加入列表的文章；当前 Hacker News 在前，RSS 在后。

## 运行项目

需要 Java 17、MySQL 8.0，并能访问 Maven 依赖仓库和资讯接口。先创建 `ai_daily` 数据库。新数据库可执行 `sql/schema.sql`；如果 `articles` 表已由 JPA 创建，`CREATE TABLE IF NOT EXISTS` 不会补上唯一索引，请先检查重复 URL，再按 `sql/add_url_hash_unique.sql` 中的说明执行迁移。

在 IDEA 运行配置中设置 `DB_USERNAME` 和 `DB_PASSWORD`，或在当前 PowerShell 会话设置环境变量；真实密码不要写入项目文件。在项目根目录运行：

```powershell
.\mvnw.cmd spring-boot:run
```

也可以在 IDE 中运行 `AiDailyApplication`。应用启动后执行一次采集，采集上限由 `app.collect.hn-limit` 和 `app.collect.rss-limit` 控制，默认分别为 10 和 5。清洗后保存新文章到数据库，并覆盖工作目录下的 `data/articles.json`；最终数量取决于源数据、请求结果和清洗情况。启动日志会打印最近入库的 20 篇文章。

数据库去重以清洗后的 URL 为依据。`existsByUrl` 处理通常的重复采集；`uk_articles_url_hash` 唯一索引提供数据库约束。请用 `SHOW INDEX FROM articles WHERE Key_name = 'uk_articles_url_hash';` 确认实际数据库已建索引。

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
