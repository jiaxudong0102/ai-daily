package com.example.aidaily.client;


import com.example.aidaily.entity.ArticleEntity;
import com.example.aidaily.response.ChatResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;



import java.util.List;
import java.util.Map;
@Component
public class DeepSeekClient {
    private final RestClient restClient;
    private final String BASE_URL ;
    private final  String kpi;

    public DeepSeekClient(RestClient.Builder restClient, @Value("${app.ai.chat-url}")String baseUrl, @Value("${app.ai.api-key}") String kpi) {
        this.restClient = restClient.build();
        BASE_URL = baseUrl;
        this.kpi = kpi;
    }

    public Map<String, Object> RequestBodyBuilder(ArticleEntity articleEntity) {
        String articleInfo = """
                来源:%s,
                标题:%s，
                文章：%s
                """.formatted(articleEntity.getSource(), articleEntity.getTitle(), articleEntity.getText());
        Map<String, Object> requst = Map.of(
                "model", "deepseek-flash",
                "stream", false,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                        你是技术资讯编辑。
                                        仅依据用户提供的标题和来源和文章内容，写一句不超过80字的中文简讯。
                                        不要推断内容中没有的事实。
                                        分类只能从 AI、后端、前端、工具、其他 中选择一个。
                                        只返回JSON对象，包含summary和category两个字段；
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
                .uri(BASE_URL)
                .headers(headers -> headers.setBearerAuth(kpi))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

            String content = response.choices().get(0).message().content();


        return content;
    }
}