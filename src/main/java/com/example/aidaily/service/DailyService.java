package com.example.aidaily.service;

import com.example.aidaily.common.AiStatus;
import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyService {
    private final ArticleRepository articleRepository;

    public DailyService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    public List<ArticleEntity> getArticles(LocalDate date) {
        return articleRepository.findAiCandidate(AiStatus.SUCCESS, date);
    }
}
