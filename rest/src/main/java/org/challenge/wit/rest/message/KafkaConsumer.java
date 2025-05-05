package org.challenge.wit.rest.message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableKafka
public class KafkaConsumer {
    private final Map<String, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    public CompletableFuture<String> prepareResponse(String correlationId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = "calculator-response-topic", groupId = "calculator-response-group")
    public void consume(String message) {
        log.info("Consumed message from calculator: {}", message);
        String[] parts = message.split("\\|", 2);
        if (parts.length < 2) {
            log.error("Invalid message format: {}", message);
            return;
        }

        String correlationId = parts[0];
        String result = parts[1];

        MDC.put("correlationId", correlationId);
        try {
            log.info("Correlation ID restored in MDC");
            log.info("Calculation result: {}", result);
            CompletableFuture<String> future = futures.remove(correlationId);
            if (future != null) {
                future.complete(result);
            }
        } finally {
            MDC.remove("correlationId");
        }
    }
}
