package com.example.aidaily.service;

import com.example.aidaily.model.Article;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleService {
    public List<Article> clean(List<Article> articles) {
        Map<String, Article> articleMap = new LinkedHashMap<>();
        for (Article article : articles) {
            if(!isValid(article)) {
                continue;
            }
            Article article1 =normalize(article);
            articleMap.putIfAbsent(article1.url(), article1);
        }
        return new ArrayList<>(articleMap.values());
    }
    private boolean isValid(Article article) {
        return article!=null
                && article.title()!=null
                &&! article.title().isBlank()
                && article.url()!=null
                && ! article.url().isBlank();
    }
    private Article normalize(Article article) {
        return new Article(
                article.id(),
                article.title().strip(),
                article.url().strip(),
                article.time(),
                article.source(),
                article.text()
        );
    }

}
