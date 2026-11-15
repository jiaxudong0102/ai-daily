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
                1L, "有效文章", "https://example.com/a", null, "hn");

        List<Article> input = Arrays.asList(
                null,
                new Article(2L, null, "https://example.com/b", null, "rss"),
                new Article(3L, "   ", "https://example.com/c", null, "rss"),
                new Article(4L, "无链接", null, null, "rss"),
                new Article(5L, "空白链接", "   ", null, "rss"),
                valid
        );

        assertEquals(List.of(valid), articleService.clean(input));
    }

    @Test
    void shouldNormalizeAndKeepFirstArticleForEachUrl() {
        List<Article> input = List.of(
                new Article(
                        1L, "  文章 A  ",
                        "  https://example.com/a  ", 100L, "hn"),
                new Article(
                        2L, "文章 A 的另一个标题",
                        "https://example.com/a", 200L, "rss"),
                new Article(
                        3L, "文章 B",
                        "https://example.com/b", 300L, "rss")
        );

        List<Article> expected = List.of(
                new Article(
                        1L, "文章 A",
                        "https://example.com/a", 100L, "hn"),
                new Article(
                        3L, "文章 B",
                        "https://example.com/b", 300L, "rss")
        );

        assertEquals(expected, articleService.clean(input));
    }

    @Test
    void shouldHandleEmptyList() {
        assertEquals(List.of(), articleService.clean(List.of()));
    }
}
