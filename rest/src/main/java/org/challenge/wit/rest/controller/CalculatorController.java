package org.challenge.wit.rest.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.rest.message.KafkaConsumer;
import org.challenge.wit.rest.message.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/calculator")
@EnableKafka
@Slf4j
public class CalculatorController {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private KafkaConsumer kafkaConsumer;

    @GetMapping("/sum")
    public String sum(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|sum" + a + ":" + b;
        return sendMessage(message, correlationId);
    }

    @GetMapping("/sub")
    public String sub(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|sub" + a + ":" + b;
        return sendMessage(message, correlationId);
    }

    @GetMapping("/multi")
    public String multi(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|multi" + a + ":" + b;
        return sendMessage(message, correlationId);
    }

    @GetMapping("/div")
    public String div(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|div" + a + ":" + b;
        return sendMessage(message, correlationId);
    }

    public String sendMessage(String message, String correlationId) {
        CompletableFuture<String> future = kafkaConsumer.prepareResponse(correlationId);
        kafkaProducer.send(message);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}
