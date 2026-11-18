package com.example.aidaily.service;

import com.example.aidaily.client.DeepSeekClient;
import com.example.aidaily.common.AiStatus;
import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiProcessingServiceTest {
    private final DeepSeekClient client = mock(DeepSeekClient.class);
    private final ArticleRepository repository = mock(ArticleRepository.class);
    private final AiProcessingService service =
            new AiProcessingService(client, new ObjectMapper(), repository,
                    new InterestScoringService("后端", "Spring,Java"));

    @Test
    void savesEachArticleAndContinuesAfterOneFails() {
        ArticleEntity failed = article(1L, 0L);
        ArticleEntity succeeded = article(2L, 0L);
        when(repository.findAiCandidates(eq(AiStatus.PENDING), eq(AiStatus.FAILED),
                eq(3L), any(Pageable.class))).thenReturn(List.of(failed, succeeded));
        when(client.HttpRequest(failed)).thenThrow(new IllegalStateException("模拟接口超时"));
        when(client.HttpRequest(succeeded)).thenReturn(
                "{\"summary\":\"介绍 Spring 新功能\",\"category\":\"后端\"}");

        service.processBatch(2);

        assertEquals(AiStatus.FAILED, failed.getAiStatus());
        assertEquals(1L, failed.getRetryCount());
        assertEquals(AiStatus.SUCCESS, succeeded.getAiStatus());
        assertEquals("介绍 Spring 新功能", succeeded.getSummary());
        assertEquals("后端", succeeded.getCategory());
        assertEquals(7L, succeeded.getInterestScore());
        assertNull(succeeded.getLastError());
        verify(repository).save(failed);
        verify(repository).save(succeeded);
    }

    @Test
    void treatsMissingRetryCountAsZeroAndRejectsInvalidCategory() {
        ArticleEntity article = article(3L, null);
        when(repository.findAiCandidates(eq(AiStatus.PENDING), eq(AiStatus.FAILED),
                eq(3L), any(Pageable.class))).thenReturn(List.of(article));
        when(client.HttpRequest(article)).thenReturn(
                "{\"summary\":\"有内容\",\"category\":\"未知类别\"}");

        service.processBatch(1);

        assertEquals(AiStatus.FAILED, article.getAiStatus());
        assertEquals(1L, article.getRetryCount());
        assertNull(article.getSummary());
        verify(repository).save(article);
    }

    @Test
    void skipsAlreadySuccessfulArticle() {
        ArticleEntity article = article(4L, 0L);
        article.setAiStatus(AiStatus.SUCCESS);
        when(repository.findAiCandidates(eq(AiStatus.PENDING), eq(AiStatus.FAILED),
                eq(3L), any(Pageable.class))).thenReturn(List.of(article));

        service.processBatch(1);

        verify(client, never()).HttpRequest(article);
        verify(repository, never()).save(article);
    }

    @Test
    void retriesFailedArticleAndSavesSuccessfulResult() {
        ArticleEntity retrying = article(5L, 1L);
        retrying.setAiStatus(AiStatus.FAILED);
        when(repository.findAiCandidates(eq(AiStatus.PENDING), eq(AiStatus.FAILED),
                eq(3L), any(Pageable.class))).thenReturn(List.of(retrying));
        when(client.HttpRequest(retrying)).thenReturn(
                "{\"summary\":\"介绍了新功能\",\"category\":\"后端\"}");

        service.processBatch(1);

        assertEquals(AiStatus.SUCCESS, retrying.getAiStatus());
        assertEquals(1L, retrying.getRetryCount());
        assertEquals("介绍了新功能", retrying.getSummary());
        assertEquals("后端", retrying.getCategory());
        assertNull(retrying.getLastError());
        verify(repository).save(retrying);
    }

    @Test
    void incrementsFailureCountAndSavesErrorWhenRetryFailsAgain() {
        ArticleEntity retrying = article(6L, 1L);
        retrying.setAiStatus(AiStatus.FAILED);
        when(repository.findAiCandidates(eq(AiStatus.PENDING), eq(AiStatus.FAILED),
                eq(3L), any(Pageable.class))).thenReturn(List.of(retrying));
        when(client.HttpRequest(retrying)).thenThrow(new IllegalStateException("模拟接口超时"));

        service.processBatch(1);

        assertEquals(AiStatus.FAILED, retrying.getAiStatus());
        assertEquals(2L, retrying.getRetryCount());
        assertTrue(retrying.getLastError().contains("模拟接口超时"));
        verify(repository).save(retrying);
    }

    private ArticleEntity article(Long id, Long retryCount) {
        ArticleEntity article = new ArticleEntity();
        article.setId(id);
        article.setTitle("Spring 新闻");
        article.setText("文章正文");
        article.setAiStatus(AiStatus.PENDING);
        article.setRetryCount(retryCount);
        return article;
    }
}
