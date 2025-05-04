package org.challenge.wit.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public double sum(double a, double b) {
        return a + b;
    }

    public double sub(double a, double b) {
        return a - b;
    }

    public double div(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }

    public double multi(double a, double b) {
        return a * b;
    }
}
