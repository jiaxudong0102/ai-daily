package com.example.aidaily.service;

import com.example.aidaily.model.Article;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleServiceTest {

    private final ArticleService articleService = new ArticleService();

    @Test
    void shouldRemoveInvalidArticles() {
        Article valid = new Article(
                1L, "有效文章", "https://example.com/a", null, "hn", "有效正文");

        List<Article> input = Arrays.asList(
                null,
                new Article(2L, null, "https://example.com/b", null, "rss", null),
                new Article(3L, "   ", "https://example.com/c", null, "rss", null),
                new Article(4L, "无链接", null, null, "rss", null),
                new Article(5L, "空白链接", "   ", null, "rss", null),
                valid
        );

        assertEquals(List.of(valid), articleService.clean(input));
    }

    @Test
    void shouldNormalizeAndKeepFirstArticleForEachUrl() {
        List<Article> input = List.of(
                new Article(
                        1L, "  文章 A  ",
                        "  https://example.com/a  ", 100L, "hn", "正文 A"),
                new Article(
                        2L, "文章 A 的另一个标题",
                        "https://example.com/a", 200L, "rss", "重复文章的正文"),
                new Article(
                        3L, "文章 B",
                        "https://example.com/b", 300L, "rss", null)
        );

        List<Article> expected = List.of(
                new Article(
                        1L, "文章 A",
                        "https://example.com/a", 100L, "hn", "正文 A"),
                new Article(
                        3L, "文章 B",
                        "https://example.com/b", 300L, "rss", null)
        );

        assertEquals(expected, articleService.clean(input));
    }

    @Test
    void shouldHandleEmptyList() {
        assertEquals(List.of(), articleService.clean(List.of()));
    }
}
