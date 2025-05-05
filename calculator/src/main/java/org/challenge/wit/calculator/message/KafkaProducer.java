package org.challenge.wit.calculator.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
@Slf4j
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        log.info("Producing message to rest: {}", message);
        kafkaTemplate.send("calculator-response-topic", message);
    }
}
