package com.example.aidaily.runner;

import com.example.aidaily.collecter.HackerNewsCollector;
import com.example.aidaily.collecter.RssCollector;
import com.example.aidaily.model.Article;
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
    public final RssCollector rssCollector;
    public CollectionRunner(HackerNewsCollector collector, ObjectMapper objectMapper, RssCollector rssCollector) {
        this.collector = collector;
        this.objectMapper = objectMapper;
        this.rssCollector = rssCollector;

    }
    @Override
    public void run(String... args) throws Exception {
        List<Article> articly1 =collector.fetch(10);
        List<Article> articly2 =rssCollector.fetch(5);
        List<Article> articlies=new ArrayList<>();
        articlies.addAll(articly1);
        articlies.addAll(articly2);


        ObjectReader reader = objectMapper.reader();
        Path output =Path.of("data","articles.json");
        Files.createDirectories(output.getParent());

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(output.toFile(), articlies);

        System.out.printf(
                "成功采集 %d 条资讯，保存到 %s%n",
                articlies.size(),
                output
        );
    }
}
