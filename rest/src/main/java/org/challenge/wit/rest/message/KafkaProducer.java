package org.challenge.wit.rest.message;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.challenge.wit.rest.model.OperationMessage;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@EnableKafka
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, OperationMessage> kafkaTemplate;

    public void send(OperationMessage message) {
        String correlationId = MDC.get("correlationId");
        message.setCorrelationId(correlationId);
        kafkaTemplate.send("calculator-topic", message);
    }

}
