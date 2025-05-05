package org.challenge.wit.rest.message;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaConsumerTest {

    @Test
    public void testPrepareResponseStoresFuture() {
        KafkaConsumer consumer = new KafkaConsumer();
        String correlationId = "abc123";

        CompletableFuture<String> future = consumer.prepareResponse(correlationId);

        assertNotNull(future);
        consumer.consume("abc123|42.0");

        assertTrue(future.isDone());
        assertEquals("42.0", future.join());
    }

    @Test
    public void testFutureTimesOutIfNotConsumed() {
        KafkaConsumer consumer = new KafkaConsumer();
        String correlationId = "timeout-id";

        CompletableFuture<String> future = consumer.prepareResponse(correlationId);

        assertThrows(Exception.class, () -> {
            future.get(100, TimeUnit.MILLISECONDS);
        });
    }
}


