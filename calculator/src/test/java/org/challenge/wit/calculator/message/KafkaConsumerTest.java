package org.challenge.wit.calculator.message;

import org.challenge.wit.calculator.model.OperationMessage;
import org.challenge.wit.calculator.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerTest {

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private KafkaProducer kafkaProducer;

    private KafkaConsumer kafkaConsumer;

    @Captor
    private ArgumentCaptor<String> captor;

    @BeforeEach
    public void setUp() {
        kafkaConsumer = new KafkaConsumer(calculatorService);
        kafkaConsumer.kafkaProducer = kafkaProducer;
    }

    @Test
    void testConsumeValidMessageForSum() {
        OperationMessage message = new OperationMessage("123", "sum", 5.0, 3.0);
        when(calculatorService.sum(5.0, 3.0)).thenReturn(8.0);

        kafkaConsumer.consume(message);

        verify(kafkaProducer).send(captor.capture());
        String resultMessage = captor.getValue();
        assertEquals("123|8.0", resultMessage);
    }

    @Test
    void testConsumeValidMessageForSub() {
        OperationMessage message = new OperationMessage("123", "sub", 5.0, 3.0);
        when(calculatorService.sub(5.0, 3.0)).thenReturn(2.0);

        kafkaConsumer.consume(message);

        verify(kafkaProducer).send(captor.capture());
        String resultMessage = captor.getValue();
        assertEquals("123|2.0", resultMessage);
    }

    @Test
    void testConsumeInvalidOperation() {
        OperationMessage message = new OperationMessage("123", "invalid", 5.0, 3.0);

        assertThrows(IllegalArgumentException.class, () -> kafkaConsumer.consume(message));
    }

    @Test
    void testConsumeDivisionByZero() {
        OperationMessage message = new OperationMessage("123", "div", 5.0, 0.0);
        when(calculatorService.div(5.0, 0.0)).thenThrow(new ArithmeticException("Division by zero"));

        assertThrows(ArithmeticException.class, () -> kafkaConsumer.consume(message));
    }

}

