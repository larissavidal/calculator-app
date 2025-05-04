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
        String[] parts = message.split(":");
        String operation = parts[0];
        double a = Double.parseDouble(parts[1]);
        double b = Double.parseDouble(parts[2]);
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
        String response = "Result of operation: " + operation + ": " + result;
        kafkaProducer.send(response);
        log.info("Sent message: {}", response);
    }
}
