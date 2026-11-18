package com.example.aidaily.service;

import com.example.aidaily.client.DeepSeekClient;
import com.example.aidaily.common.AiStatus;
import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.repository.ArticleRepository;
import com.example.aidaily.result.aiResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "app.ai", name = "enabled", havingValue = "true")
public class AiProcessingService {
    private static final long MAX_RETRIES = 3L;
    private static final Set<String> CATEGORIES = Set.of("AI", "后端", "前端", "工具", "其他");

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final InterestScoringService interestScoringService;

    public AiProcessingService(DeepSeekClient deepSeekClient, ObjectMapper objectMapper,
                               ArticleRepository articleRepository,
                               InterestScoringService interestScoringService) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.interestScoringService = interestScoringService;
    }

    public void processBatch(int limit) {
        if (limit <= 0) {
            return;
        }
        var articles = articleRepository.findAiCandidates(
                AiStatus.PENDING, AiStatus.FAILED, MAX_RETRIES, PageRequest.of(0, limit));
        for (ArticleEntity article : articles) {
            processOne(article);
        }
    }

    public List<ArticleEntity> findTopRanked(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return articleRepository.findByAiStatus(AiStatus.SUCCESS,
                PageRequest.of(0, limit, Sort.by(Sort.Order.desc("interestScore"),
                        Sort.Order.desc("id"))));
    }

    private void processOne(ArticleEntity article) {
        long failures = article.getRetryCount() == null ? 0L : article.getRetryCount();
        if (article.getAiStatus() == AiStatus.SUCCESS || failures >= MAX_RETRIES) {
            return;
        }

        try {
            String content = deepSeekClient.HttpRequest(article);
            aiResult result = objectMapper.readValue(content, aiResult.class);
            if (result == null || result.summary() == null || result.summary().isBlank()) {
                throw new IllegalStateException("摘要为空");
            }
            String category = result.category() == null ? null : result.category().strip();
            if (category == null || !CATEGORIES.contains(category)) {
                throw new IllegalStateException("分类无效");
            }

            article.setSummary(result.summary().strip());
            article.setCategory(category);
            article.setInterestScore(interestScoringService.score(article));
            article.setAiStatus(AiStatus.SUCCESS);
            article.setLastError(null);
        } catch (Exception exception) {
            article.setAiStatus(AiStatus.FAILED);
            article.setRetryCount(failures + 1);
            String message = exception.getMessage();
            String error = exception.getClass().getSimpleName() + ": " +
                    (message == null ? "未知错误" : message);
            article.setLastError(error.substring(0, Math.min(error.length(), 250)));
            System.err.printf("文章 AI 处理失败，id=%d，原因=%s%n", article.getId(), article.getLastError());
        }

        // The database write is outside the API/parse catch so a database error is not
        // misreported as a DeepSeek failure. Each article is saved independently.
        articleRepository.save(article);
    }
}
