package org.challenge.wit.rest.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaConsumer {
    private final Map<String, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    public CompletableFuture<String> prepareResponse(String correlationId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        futures.put(correlationId, future);
        return future;
    }

    @KafkaListener(topics = "calculator-response-topic", groupId = "calculator-group")
    public void consume(String message) {
        log.info("Consumed message on rest module: {}", message);
        String[] parts = message.split("\\|", 2);
        if (parts.length < 2) return;

        String correlationId = parts[0];
        String result = parts[1];

        CompletableFuture<String> future = futures.remove(correlationId);
        if (future != null) {
            future.complete(result);
        };
    }
}
