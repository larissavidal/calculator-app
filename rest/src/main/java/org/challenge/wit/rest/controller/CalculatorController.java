package org.challenge.wit.rest.controller;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.rest.message.*;
import org.challenge.wit.rest.model.OperationMessage;
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
        OperationMessage message = new OperationMessage(correlationId, "sum", a, b);
        return sendMessage(message);
    }

    @GetMapping("/sub")
    public String sub(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "sub", a, b);
        return sendMessage(message);
    }

    @GetMapping("multi")
    public String multi(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "multi", a, b);
        return sendMessage(message);
    }

    @GetMapping("/div")
    public String div(@RequestParam double a, @RequestParam double b, HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();
        response.setHeader("X-Correlation-ID", correlationId);
        OperationMessage message = new OperationMessage(correlationId, "div", a, b);
        return sendMessage(message);
    }

    private String sendMessage(OperationMessage message){
        CompletableFuture<String> future = kafkaConsumer.prepareResponse(message.getCorrelationId());
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
