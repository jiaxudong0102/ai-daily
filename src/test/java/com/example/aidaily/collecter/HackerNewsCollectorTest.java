package com.example.aidaily.collecter;

import com.example.aidaily.model.Article;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HackerNewsCollectorTest {
    @Test
    void acceptsOrdinaryStoryWithoutText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        HackerNewsCollector collector = new HackerNewsCollector(builder);
        server.expect(requestTo("https://hacker-news.firebaseio.com/v0/topstories.json"))
                .andRespond(withSuccess("[50538240]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://hacker-news.firebaseio.com/v0/item/50538240.json"))
                .andRespond(withSuccess("""
                        {"by":"someone","descendants":12,"id":50538240,"kids":[1,2],
                         "score":80,"time":1795000000,"title":"A normal story",
                         "type":"story","url":"https://example.com/story"}
                        """, MediaType.APPLICATION_JSON));

        List<Article> articles = collector.fetch(1);

        assertEquals(1, articles.size());
        assertEquals("A normal story", articles.get(0).title());
        assertNull(articles.get(0).text());
        server.verify();
    }
}
