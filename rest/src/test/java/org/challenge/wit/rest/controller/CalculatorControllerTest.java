package org.challenge.wit.rest.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.challenge.wit.rest.message.KafkaConsumer;
import org.challenge.wit.rest.message.KafkaProducer;
import org.challenge.wit.rest.model.OperationMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import java.util.concurrent.CompletableFuture;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CalculatorControllerTest {
    @InjectMocks
    private CalculatorController controller;

    @Mock
    private KafkaConsumer kafkaConsumer;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
        reset(kafkaProducer, kafkaConsumer, response);
    }

    @ParameterizedTest
    @CsvSource({
            "sum, 10.0, 5.0, 15.0",
            "sub, 10.0, 5.0, 5.0",
            "multi, 10.0, 5.0, 50.0",
            "div, 10.0, 5.0, 2.0"
    })
    public void testOperations(String operation, double a, double b, String expectedResult) throws Exception {

        CompletableFuture<String> future = CompletableFuture.completedFuture(expectedResult);
        when(kafkaConsumer.prepareResponse(anyString())).thenReturn(future);

        MDC.put("correlationId", UUID.randomUUID().toString());

        ResponseEntity<?> result = switch (operation) {
            case "sum" -> controller.sum(a, b, response);
            case "sub" -> controller.sub(a, b, response);
            case "multi" -> controller.multi(a, b, response);
            case "div" -> controller.div(a, b, response);
            default -> throw new IllegalArgumentException("Invalid operation");
        };

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals(expectedResult, body.get("result"));

        ArgumentCaptor<OperationMessage> captor = ArgumentCaptor.forClass(OperationMessage.class);
        verify(kafkaProducer, times(1)).send(captor.capture());

        OperationMessage sentMessage = captor.getValue();
        assertEquals(operation, sentMessage.getOperation());
        assertEquals(a, sentMessage.getA());
        assertEquals(b, sentMessage.getB());

        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
    }

    @Test
    public void testDivisionByZero() throws Exception {
        double a = 10.0;
        double b = 0.0;

        MDC.put("correlationId", UUID.randomUUID().toString());

        CompletableFuture<String> future = mock(CompletableFuture.class);
        when(kafkaConsumer.prepareResponse(anyString())).thenReturn(future);
        when(future.get(anyLong(), any())).thenThrow(new ArithmeticException("Division by zero"));

        ResponseEntity<?> result = controller.div(a, b, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals("Internal error: Division by zero", body.get("error"));

        verify(kafkaProducer).send(any(OperationMessage.class));
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
    }
}
