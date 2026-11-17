package com.example.aidaily.runner;

import com.example.aidaily.service.AiProcessingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)

public class AiProcessingRunner implements CommandLineRunner {
    private final AiProcessingService service;
    public AiProcessingRunner(AiProcessingService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        service.processBatch(8);

    }
}
