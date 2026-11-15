package com.example.aidaily.collecter;

import com.example.aidaily.model.Article;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class HackerNewsCollector {
    private static final String TOP_STORIES="https://hacker-news.firebaseio.com/v0/topstories.json";
    private static final String ITEM="https://hacker-news.firebaseio.com/v0/item/{id}.json";
    private final RestClient restClient;
    public HackerNewsCollector(RestClient.Builder builder) {
        this.restClient = builder.build();
    }
    /**
     * 按数量推荐
     */
    public List<Article>fetch(int limit) {
        Long[] ids=restClient.get().uri(TOP_STORIES).retrieve().body(Long[].class);
        if(ids==null){
            return List.of();
        }
        return Arrays.stream(ids)
                .limit(limit)
                .map(this::fetchArticle)
                .filter(Objects::nonNull)
                .toList();
    }
    private Article fetchArticle(Long id1){
        try {
            Article article1=restClient.get().uri(ITEM, id1).retrieve().body(Article.class);
            if(article1==null){
                return null;
            }
            return new Article(
                    article1.id(),
                    article1.title(),
                    article1.url(),
                    article1.time(),
                    "hn"
            );
        }
        catch (RestClientException exception) {
                System.err.printf(
                        "文章采集失败，id=%d,原因=%s\n",id1,exception.getMessage()
                );
                return null;
        }
    }


}