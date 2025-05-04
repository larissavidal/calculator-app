package org.challenge.wit.calculator.message;

import lombok.extern.slf4j.Slf4j;
import org.challenge.wit.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
@Slf4j
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
        log.info("Received message: {}", message);
        String[] parts = message.split("\\|", 2);
        if (parts.length < 2) return;

        String correlationId = parts[0];
        String operationMessage = parts[1];
        String[] operationParts = operationMessage.split(":");

        String operation = operationParts[0];
        double a = Double.parseDouble(operationParts[1]);
        double b = Double.parseDouble(operationParts[2]);

        double result = 0;
        switch (operation.toLowerCase()) {
            case "sum":
                result = calculatorService.sum(a, b);
                break;
            case "sub":
                result = calculatorService.sub(a, b);
                break;
            case "div":
                result = calculatorService.div(a, b);
                break;
            case "multi":
                result = calculatorService.multi(a, b);
                break;
            default:
                log.error("Invalid operation: {}", operation);
                return;
        };
        String response = correlationId + "|" + result;
        kafkaProducer.send(response);
        log.info("Sent message: {}", response);
    }
}
