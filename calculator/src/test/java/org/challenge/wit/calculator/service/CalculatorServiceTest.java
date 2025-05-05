package org.challenge.wit.calculator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CalculatorServiceTest {
    @InjectMocks
    private CalculatorService calculatorService;

    @Test
    void testSum() {
        double result = calculatorService.sum(1, 2);
        assertEquals(3.0, result);
    }

    @Test
    void testSumWithZero() {
        assertEquals(5.0, calculatorService.sum(5, 0));
        assertEquals(5.0, calculatorService.sum(0, 5));
    }

    @Test
    void testSub() {
        double result = calculatorService.sub(1, 2);
        assertEquals(-1.0, result);
    }

    @Test
    void testSubWithZero() {
        assertEquals(5.0, calculatorService.sub(5, 0));
        assertEquals(-5.0, calculatorService.sub(0, 5));
    }

    @Test
    void testMultiply() {
        double result = calculatorService.multi(1, 2);
        assertEquals(2.0, result);
    }

    @Test
    void testMultiplyWithZero() {
        assertEquals(0.0, calculatorService.multi(0, 10));
        assertEquals(0.0, calculatorService.multi(10, 0));
    }


    @Test
    void testMultiplyWithOne() {
        assertEquals(10.0, calculatorService.multi(1, 10));
        assertEquals(10.0, calculatorService.multi(10, 1));
    }

    @Test
    void testDivide() {
        double result = calculatorService.div(1, 2);
        assertEquals(0.5, result);
    }

    @Test
    void testDivideByOne() {
        assertEquals(10.0, calculatorService.div(10, 1));
    }

    @Test
    void testDivideByZero() {
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculatorService.div(1, 0)
        );

        assertEquals("Division by zero", exception.getMessage());
    }

    @Test
    void testDividePrecision() {
        double result = calculatorService.div(1, 3);
        assertEquals(0.3333, result, 0.0001);
    }

    @Test
    void testNegativeNumbers() {
        assertEquals(-1.0, calculatorService.sum(1, -2));
        assertEquals(3.0, calculatorService.sub(1, -2));
        assertEquals(-2.0, calculatorService.multi(1, -2));
        assertEquals(-0.5, calculatorService.div(1, -2));
    }

}
