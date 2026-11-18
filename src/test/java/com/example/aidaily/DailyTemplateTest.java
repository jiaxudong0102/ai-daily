package com.example.aidaily;

import com.example.aidaily.entity.ArticleEntity;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DailyTemplateTest {
    @Test
    void rendersArticleDataAndEscapesExternalText() {
        ArticleEntity article = new ArticleEntity();
        article.setTitle("Spring <新功能>");
        article.setSummary("摘要包含 <script> 标签");
        article.setCategory("后端");
        article.setSource("rss");
        article.setUrl("https://spring.io/blog/example");
        article.setInterestScore(7L);

        String html = render("2026-11-18", List.of(article));

        assertTrue(html.contains("Spring &lt;新功能&gt;"));
        assertTrue(html.contains("摘要包含 &lt;script&gt; 标签"));
        assertTrue(html.contains("https://spring.io/blog/example"));
        assertTrue(html.contains("篇精选文章"));
        assertFalse(html.contains("这一天还没有已处理的文章"));
    }

    @Test
    void rendersEmptyStateWhenNoArticlesExist() {
        String html = render("2026-11-17", List.of());

        assertTrue(html.contains("这一天还没有已处理的文章"));
        assertTrue(html.contains("2026-11-17"));
    }

    private String render(String date, List<ArticleEntity> articles) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        Context context = new Context();
        context.setVariable("date", date);
        context.setVariable("articles", articles);
        return engine.process("daily", context);
    }
}
