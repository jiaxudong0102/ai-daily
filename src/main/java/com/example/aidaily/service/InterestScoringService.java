package com.example.aidaily.service;

import com.example.aidaily.entity.ArticleEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class InterestScoringService {
    private final List<String> categories;
    private final List<String> keywords;

    public InterestScoringService(@Value("${app.interest.categories:}") String categories,
                                  @Value("${app.interest.keywords:}") String keywords) {
        this.categories = split(categories);
        this.keywords = split(keywords);
    }

    public long score(ArticleEntity article) {
        long score = 0;
        String category = article.getCategory() == null ? "" : article.getCategory();
        if (categories.contains(category.toLowerCase(Locale.ROOT))) {
            score += 5;
        }
        String searchable = ((article.getTitle() == null ? "" : article.getTitle()) + " " +
                (article.getSummary() == null ? "" : article.getSummary()))
                .toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (searchable.contains(keyword)) {
                score += 2;
            }
        }
        return score;
    }

    private List<String> split(String value) {
        return Arrays.stream(value.split(","))
                .map(String::strip)
                .filter(item -> !item.isEmpty())
                .map(item -> item.toLowerCase(Locale.ROOT))
                .toList();
    }
}
