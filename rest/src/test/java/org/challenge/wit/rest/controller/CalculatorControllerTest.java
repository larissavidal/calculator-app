package org.challenge.wit.rest.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.challenge.wit.rest.message.KafkaConsumer;
import org.challenge.wit.rest.message.KafkaProducer;
import org.challenge.wit.rest.model.OperationMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CalculatorControllerTest {
    private KafkaProducer kafkaProducer;
    private KafkaConsumer kafkaConsumer;
    private CalculatorController controller;
    private HttpServletResponse response;

    @BeforeEach
    public void setup() {
        kafkaProducer = mock(KafkaProducer.class);
        kafkaConsumer = mock(KafkaConsumer.class);
        response = mock(HttpServletResponse.class);

        controller = new CalculatorController();

        inject(controller, "kafkaProducer", kafkaProducer);
        inject(controller, "kafkaConsumer", kafkaConsumer);
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

        String result = switch (operation) {
            case "sum" -> controller.sum(a, b, response);
            case "sub" -> controller.sub(a, b, response);
            case "multi" -> controller.multi(a, b, response);
            case "div" -> controller.div(a, b, response);
            default -> throw new IllegalArgumentException("Invalid operation");
        };

        assertEquals(expectedResult, result);

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

        CompletableFuture<String> future = mock(CompletableFuture.class);
        when(kafkaConsumer.prepareResponse(anyString())).thenReturn(future);
        when(future.get(anyLong(), any())).thenThrow(new ArithmeticException("Division by zero"));

        String result = controller.div(a, b, response);

        assertEquals("Division by zero", result);

        verify(kafkaProducer).send(any(OperationMessage.class));
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
    }

    private void inject(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
