package com.example.aidaily.runner;

import com.example.aidaily.collecter.HackerNewsCollector;
import com.example.aidaily.collecter.RssCollector;
import com.example.aidaily.model.Article;
import com.example.aidaily.service.ArticleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

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

    public CollectionRunner(HackerNewsCollector collector, ObjectMapper objectMapper, RssCollector rssCollector, ArticleService articleService) {
        this.collector = collector;
        this.objectMapper = objectMapper;
        this.rssCollector = rssCollector;

        this.articleService = articleService;
    }
    @Override
    public void run(String... args) throws Exception {
        List<Article> hnarticly =collector.fetch(10);
        List<Article> rssarticly =rssCollector.fetch(5);
        List<Article> articlies=new ArrayList<>();
        articlies.addAll(hnarticly);
        articlies.addAll(rssarticly);
        List<Article> articly =articleService.clean(articlies);


        ObjectReader reader = objectMapper.reader();
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
