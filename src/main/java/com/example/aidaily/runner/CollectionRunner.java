package com.example.aidaily.runner;

import com.example.aidaily.collecter.HackerNewsCollector;
import com.example.aidaily.collecter.RssCollector;
import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.model.Article;
import com.example.aidaily.repository.ArticleRepository;
import com.example.aidaily.service.ArticlePersistenceService;
import com.example.aidaily.service.ArticleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CollectionRunner implements CommandLineRunner {
    private final HackerNewsCollector collector;
    private final ObjectMapper objectMapper;
    private final RssCollector rssCollector;
    private final ArticleService articleService;
    private final ArticlePersistenceService articlePersistenceService ;
    private final ArticleRepository articleRepository;
    private final int hnLimit;
    private final int rssLimit;


    public CollectionRunner(HackerNewsCollector collector, ObjectMapper objectMapper, RssCollector rssCollector, ArticleService articleService, ArticlePersistenceService articlePersistenceService, ArticleRepository articleRepository,
    @Value("${app.collect.hn-limit}") int hnLimit,
    @Value("${app.collect.rss-limit}") int rssLimit
    ) {
        this.collector = collector;
        this.objectMapper = objectMapper;
        this.rssCollector = rssCollector;

        this.articleService = articleService;
        this.articlePersistenceService = articlePersistenceService;
        this.articleRepository = articleRepository;
        this.hnLimit = hnLimit;
        this.rssLimit = rssLimit;
    }
    @Override
    public void run(String... args) throws Exception {
        List<Article> hnarticly =collector.fetch(hnLimit);
        List<Article> rssarticly =rssCollector.fetch(rssLimit);
        List<Article> articlies=new ArrayList<>();
        articlies.addAll(hnarticly);
        articlies.addAll(rssarticly);
        List<Article> articly =articleService.clean(articlies);
        for(Article article:articly){
            if(articleRepository.existsByUrl(article.url())){
                continue;
            }
           ArticleEntity articleEntity= articlePersistenceService.savedEntity(article);
            articleRepository.save(articleEntity);

        }
        List<ArticleEntity> recent=articleRepository.findTop20ByOrderByIdDesc();
        for(ArticleEntity article:recent){
            System.out.printf("%d,%s,%s%n",article.getId(),article.getSource(),article.getTitle());
        }



        Path output =Path.of("data","articles.json");
        Files.createDirectories(output.getParent());

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(output.toFile(), articly);

        System.out.printf(
                "成功采集 %d 条资讯，清洗后剩余%d条，保存到 %s%n",
                articlies.size(),
                articly.size(),
                output
        );
    }
}
