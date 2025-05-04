package org.challenge.wit.calculator.model;

import lombok.Getter;

@Getter
public class OperationMessage {
  private String correlationId;
  private String operation;
  private double a;
  private double b;
}
