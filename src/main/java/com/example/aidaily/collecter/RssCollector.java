package com.example.aidaily.collecter;

import com.example.aidaily.model.Article;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


import java.io.StringReader;
import java.util.List;

@Component
public class RssCollector {
    private static final String feedUrl = "https://news.ycombinator.com/rss";
    private final RestClient restClientBuilder;

    public RssCollector(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder.build();
    }

    public List<Article> fetch(int limit) throws FeedException {

            String xml=restClientBuilder.get()
                    .uri(feedUrl)
                    .retrieve()
                    .body(String.class);



        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new StringReader(xml));
        return feed.getEntries().stream()
                    .limit(limit)
                    .map(this::toArticle)
                    .toList();

    }
   public Article toArticle(SyndEntry entry){
        String url=entry.getLink();

        Long time=entry.getPublishedDate()==null?null:entry.getPublishedDate().toInstant().getEpochSecond();

       return new Article(
               Integer.toUnsignedLong(url.hashCode()),
               entry.getTitle(),
               url,
               time,
               "rss"
       );

   }

}
