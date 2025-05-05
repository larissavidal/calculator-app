package org.challenge.wit.rest.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.rest.message.*;
import org.challenge.wit.rest.model.OperationMessage;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/calculator")
@EnableKafka
public class CalculatorController {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @GetMapping("/sum")
    public ResponseEntity<?> sum(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = MDC.get("correlationId");
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "sum", a, b);
        return sendMessage(message);
    }

    @GetMapping("/sub")
    public ResponseEntity<?> sub(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = MDC.get("correlationId");
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "sub", a, b);
        return sendMessage(message);
    }

    @GetMapping("multi")
    public ResponseEntity<?> multi(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = MDC.get("correlationId");
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "multi", a, b);
        return sendMessage(message);
    }

    @GetMapping("/div")
    public ResponseEntity<?> div(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = MDC.get("correlationId");
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "div", a, b);
        return sendMessage(message);
    }

    private ResponseEntity<?> sendMessage(OperationMessage message){
        CompletableFuture<String> future = kafkaConsumer.prepareResponse(message.getCorrelationId());
        kafkaProducer.send(message);

        try {
            log.info("Sent message: {}", message);
            String result = future.get(5, TimeUnit.SECONDS);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (TimeoutException e) {
            log.error("Timeout waiting for response", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(Map.of("error", "Timeout waiting for calculator response"));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal error: " + e.getMessage()));
        }
    }

}
