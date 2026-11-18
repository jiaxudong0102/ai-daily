package com.example.aidaily.client;


import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.response.ChatResponse;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
@Component
@ConditionalOnProperty(prefix = "app.ai", name = "enabled", havingValue = "true")
public class DeepSeekClient {
    private final RestClient restClient;
    private final String chatUrl;
    private final String apiKey;
    private final String model;

    public DeepSeekClient(RestClient.Builder restClient,
                          @Value("${app.ai.chat-url}") String chatUrl,
                          @Value("${app.ai.api-key}") String apiKey,
                          @Value("${app.ai.model}") String model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(60));
        this.restClient = restClient.requestFactory(requestFactory).build();
        this.chatUrl = chatUrl;
        this.apiKey = apiKey;
        this.model = model;
    }

    public Map<String, Object> RequestBodyBuilder(ArticleEntity articleEntity) {
        String plainText = Jsoup.parse(articleEntity.getText() == null ? "" : articleEntity.getText()).text();
        if (plainText.length() > 12_000) {
            plainText = plainText.substring(0, 12_000);
        }
        String articleInfo = """
                来源:%s,
                标题:%s，
                文章：%s
                """.formatted(articleEntity.getSource(), articleEntity.getTitle(), plainText);
        Map<String, Object> requst = Map.of(
                "model", model,
                "stream", false,
                "response_format", Map.of("type", "json_object"),
                "max_tokens", 300,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                        你是技术资讯编辑。
                                        仅依据用户提供的标题和来源和文章内容，写一句不超过80字的中文简讯。
                                        不要推断内容中没有的事实。
                                        分类只能从 AI、后端、前端、工具、其他 中选择一个。
                                        只返回JSON对象，包含summary和category两个字段；
                                        例如：{"summary":"文章介绍了一个工具的更新。","category":"工具"}
                                        不要返回Markdown代码块或其他文字。
                                        """
                        ),
                        Map.of(
                                "role", "user",
                                "content", articleInfo
                        )
                )
        );
        return requst;
    }

    public String HttpRequest(ArticleEntity articleEntity) {
        Map<String, Object> request=RequestBodyBuilder(articleEntity);

        ChatResponse response = restClient.post()
                .uri(chatUrl)
                .headers(headers -> headers.setBearerAuth(apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()
                || response.choices().get(0) == null
                || response.choices().get(0).message() == null
                || response.choices().get(0).message().content() == null
                || response.choices().get(0).message().content().isBlank()) {
            throw new IllegalStateException("DeepSeek 返回内容为空或结构不完整");
        }
        return response.choices().get(0).message().content();
    }
}
