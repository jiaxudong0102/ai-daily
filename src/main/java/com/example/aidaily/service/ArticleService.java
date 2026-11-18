package com.example.aidaily.service;

import com.example.aidaily.model.Article;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleService {
    private static final int MAX_TEXT_LENGTH = 12_000;

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
        String text = article.text() == null ? null : Jsoup.parse(article.text()).text().strip();
        if (text != null && text.isBlank()) {
            text = null;
        } else if (text != null && text.length() > MAX_TEXT_LENGTH) {
            text = text.substring(0, MAX_TEXT_LENGTH);
        }
        return new Article(
                article.id(),
                article.title().strip(),
                article.url().strip(),
                article.time(),
                article.source(),
                text
        );
    }

}
