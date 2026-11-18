package com.example.aidaily.service;

import com.example.aidaily.entity.ArticleEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestScoringServiceTest {
    @Test
    void scoresCategoryAndEachMatchingKeywordOnce() {
        InterestScoringService service = new InterestScoringService("AI,后端", "Spring,Java");
        ArticleEntity article = new ArticleEntity();
        article.setCategory("后端");
        article.setTitle("Spring 和 Java 的更新");
        article.setSummary("Spring 生态新闻");

        assertEquals(9L, service.score(article));
    }
}
