package org.challenge.wit.rest.message;

import org.challenge.wit.rest.model.OperationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, OperationMessage> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    public void testSend() {
        OperationMessage message = new OperationMessage("abc-123", "sum", 10, 20);
        kafkaProducer.send(message);

        verify(kafkaTemplate, times(1)).send("calculator-topic", message.getCorrelationId(), message);
    }
}
