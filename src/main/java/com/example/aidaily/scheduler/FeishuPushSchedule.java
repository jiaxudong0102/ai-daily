package com.example.aidaily.scheduler;

import com.example.aidaily.runner.FeishuStartupSendRunner;
import com.example.aidaily.service.DailyPushService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class FeishuPushSchedule {
    private final DailyPushService dailyPushService;

    public FeishuPushSchedule(DailyPushService dailyPushService) {
        this.dailyPushService = dailyPushService;
    }


    @Scheduled(cron = "${app.feishu.cron}", zone = "Asia/Shanghai")
    public void scheduled() throws Exception {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        dailyPushService.setText(date);


    }
}
