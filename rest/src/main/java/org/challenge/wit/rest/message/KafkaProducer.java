package org.challenge.wit.rest.message;

import org.challenge.wit.rest.model.OperationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, OperationMessage> kafkaTemplate;

    public void send(OperationMessage message) {
        kafkaTemplate.send("calculator-topic", message.getCorrelationId(), message);
    }

}
