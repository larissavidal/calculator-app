package org.challenge.wit.calculator.message;

import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableKafka
public class KafkaConsumer {
    @Autowired
    private final CalculatorService calculatorService;
    @Autowired
    private KafkaProducer kafkaProducer;

    public KafkaConsumer(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @KafkaListener(topics = "calculator-topic", groupId = "calculator-group")
    public void consume(String message) {
        log.info("Received message on calculator consumer: {}", message);
        String[] parts = message.split("\\|", 2);
        if (parts.length < 2) return;

        String correlationId = parts[0];
        String operationMessage = parts[1];
        String[] opParts = operationMessage.split(":");

        String operation = opParts[0];
        double a = Double.parseDouble(opParts[1]);
        double b = Double.parseDouble(opParts[2]);

        double result = switch (operation) {
            case "sum" -> calculatorService.sum(a, b);
            case "sub" -> calculatorService.sub(a, b);
            case "multi" -> calculatorService.multi(a, b);
            case "div" -> calculatorService.div(a, b);
            default -> {
                log.error("Invalid operation: {}", operation);
                throw new IllegalArgumentException("Invalid operation: " + operation);
            }
        };

        String response = correlationId + "|" + result;
        kafkaProducer.send(response);
        log.info("Sent response to rest topic: {}", response);
    }
}
