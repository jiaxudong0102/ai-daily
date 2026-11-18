package com.example.aidaily.service;

import com.example.aidaily.client.FeishuWebhookClient;
import com.example.aidaily.entity.ArticleEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyPushService {
    private final DailyService dailyService;
    private final FeishuWebhookClient feishuWebhookClient;


    public DailyPushService(DailyService dailyService, FeishuWebhookClient feishuWebhookClient) {
        this.dailyService = dailyService;
        this.feishuWebhookClient = feishuWebhookClient;
    }
    public void setText(LocalDate date)
    {
        List<ArticleEntity> textList=dailyService.getArticles(date);
        String text="今日有价值新闻AI日报";
        int count=1;
        for(ArticleEntity articleEntity:textList){
           text=text+"\n"+count+"\n"+articleEntity.getTitle()+"\n"+articleEntity.getSummary()
           +"\n"+articleEntity.getUrl();
           count++;

        }
        feishuWebhookClient.FeishuWebhook(text);
    }
}
