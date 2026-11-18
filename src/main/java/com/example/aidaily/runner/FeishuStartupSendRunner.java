package com.example.aidaily.runner;


import com.example.aidaily.service.DailyPushService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.ZoneId;

@Component
@Order(3)
public class FeishuStartupSendRunner implements CommandLineRunner {
    private final DailyPushService  dailyPushService;



    public FeishuStartupSendRunner(DailyPushService dailyPushService) {
        this.dailyPushService = dailyPushService;
    }
    @Override
    public void run(String... args) throws Exception {
    LocalDate date = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        dailyPushService.setText(date);

    }
}
