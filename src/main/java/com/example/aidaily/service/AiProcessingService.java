package com.example.aidaily.service;

import com.example.aidaily.client.DeepSeekClient;
import com.example.aidaily.common.AiStatus;
import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.repository.ArticleRepository;
import com.example.aidaily.result.aiResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class AiProcessingService {
    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;

    public AiProcessingService(DeepSeekClient deepSeekClient, ObjectMapper objectMapper, ArticleRepository articleRepository) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
    }
    public List<ArticleEntity> setArticleEntity(List<ArticleEntity> articleEntities) {
    for(ArticleEntity articleEntity:articleEntities){
        if(articleEntity.getAiStatus()== AiStatus.SUCCESS||articleEntity.getRetryCount()>=3){
            continue;
        }
        if(articleEntity.getRetryCount()==null){
            articleEntity.setRetryCount(0L);
        }
        String temporaryText=deepSeekClient.HttpRequest(articleEntity);
        List<String> SummaryCategory= SummaryCategorySetter(temporaryText);
        if(SummaryCategory.get(0)==null||SummaryCategory.get(0).isBlank()){
            articleEntity.setRetryCount(articleEntity.getRetryCount()+1);
            articleEntity.setAiStatus(AiStatus.FAILED);
            throw new IllegalStateException("摘要为空");

        }
        if(SummaryCategory.get(1)==null||SummaryCategory.get(1).isBlank()){
            articleEntity.setRetryCount(articleEntity.getRetryCount()+1);
            articleEntity.setAiStatus(AiStatus.FAILED);
            throw new IllegalStateException("类别为空");
        }
        articleEntity.setSummary(SummaryCategory.get(0));
        articleEntity.setCategory(SummaryCategory.get(1));
        articleEntity.setLastError(null);
        articleEntity.setAiStatus(AiStatus.SUCCESS);
        articleRepository.saveAll(setArticleEntity(articleEntities));

    }
    return articleEntities;
    }
    public List<String> SummaryCategorySetter(String temporaryText){
        aiResult airesult=objectMapper.readValue(temporaryText,aiResult.class);
        return List.of(airesult.summary(),airesult.category());

    }
    public void processBatch(int limit){
        List<ArticleEntity> articleEntities=articleRepository.findByAiStatus(AiStatus.PENDING, PageRequest.of(0,limit));
       setArticleEntity(articleEntities);

    }
    }

