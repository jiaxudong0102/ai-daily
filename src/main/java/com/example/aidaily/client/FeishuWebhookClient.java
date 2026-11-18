package com.example.aidaily.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;


@Component
public class FeishuWebhookClient {
    private final RestClient restClient;
    private final String baseUrl;

    public FeishuWebhookClient(RestClient.Builder restClient
    ,@Value("${FEISHU_WEBHOOK_URL}") String baseUrl) {
        this.restClient = restClient.build();
        this.baseUrl = baseUrl;
    }
    public void FeishuWebhook(String text) {
        Map<?,?> result=restClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "msg_type", "text",
                                "content", Map.of("text", text)
                        )
                )
                .retrieve()
                .body(Map.class);
        Object code=(result==null)?null:result.containsKey("code")?
                result.get("code"):result.get("StatusCode");
        if (!(code instanceof Number number) || number.intValue() != 0) {
            throw new IllegalStateException("飞书推送失败：" + result);
        }
    }
}
