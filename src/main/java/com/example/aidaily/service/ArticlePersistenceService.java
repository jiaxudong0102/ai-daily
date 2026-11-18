package com.example.aidaily.service;

import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.model.Article;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.example.aidaily.common.AiStatus.PENDING;

@Service
public class ArticlePersistenceService {
    public ArticleEntity savedEntity(Article article) {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setSourceId(article.id());
        articleEntity.setTitle(article.title());
        articleEntity.setUrl(article.url());
        articleEntity.setPublishedAt(article.time());
        articleEntity.setSource(article.source());
        articleEntity.setText(article.text());
        articleEntity.setAiStatus( PENDING);
        articleEntity.setRetryCount(0L);
        articleEntity.setCollectDate(LocalDate.now(ZoneId.of("Asia/Shanghai")));
        return articleEntity;
    }
}