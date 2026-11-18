package com.example.aidaily.runner;

import com.example.aidaily.service.AiProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@ConditionalOnProperty(prefix = "app.ai", name = "enabled", havingValue = "true")
public class AiProcessingRunner implements CommandLineRunner {
    private final AiProcessingService service;
    private final int batchSize;

    public AiProcessingRunner(AiProcessingService service,
                              @Value("${app.ai.batch-size:10}") int batchSize) {
        this.service = service;
        this.batchSize = batchSize;
    }

    @Override
    public void run(String... args) {
        service.processBatch(batchSize);
    }
}
