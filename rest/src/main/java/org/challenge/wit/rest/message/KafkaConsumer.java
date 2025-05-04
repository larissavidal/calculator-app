package org.challenge.wit.rest.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class KafkaConsumer {
    private String result;
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "calculator-response-topic", groupId = "calculator-group")
    public void consume(String message) {
        result = message;
        latch.countDown();
    }

    public String getResult() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
