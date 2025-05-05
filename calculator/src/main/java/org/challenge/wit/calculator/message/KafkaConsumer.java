package org.challenge.wit.calculator.message;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.challenge.wit.calculator.model.OperationMessage;
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
    protected KafkaProducer kafkaProducer;

    public KafkaConsumer(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @KafkaListener(
            topics = "calculator-topic",
            groupId = "calculator-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(OperationMessage message) {
        String correlationId = message.getCorrelationId();
        MDC.put("correlationId", correlationId);

        try {
            log.info("Received message on calculator consumer: {}", message);

            String operation = message.getOperation();
            double a = message.getA();
            double b = message.getB();

            double result = switch (operation.toLowerCase()) {
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
        } finally {
            MDC.remove("correlationId");
        }
    }
}
