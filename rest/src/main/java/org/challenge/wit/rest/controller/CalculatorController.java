package org.challenge.wit.rest.controller;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.rest.message.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String sum(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|sum:" + a + ":" + b;
        return sendMessage(correlationId, message);
    }

    @GetMapping("/sub")
    public String sub(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|sub:" + a + ":" + b;
        return sendMessage(correlationId, message);
    }

    @GetMapping("multi")
    public String multi(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|multi:" + a + ":" + b;
        return sendMessage(correlationId, message);
    }

    @GetMapping("/div")
    public String div(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        String message = correlationId + "|div:" + a + ":" + b;
        return sendMessage(correlationId, message);
    }

    private String sendMessage(String correlationId, String message){
        CompletableFuture<String> future = kafkaConsumer.prepareResponse(correlationId);
        kafkaProducer.send(message);

        try {
            log.info("Sent message: {}", message);
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

}
