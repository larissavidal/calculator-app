package org.challenge.wit.calculator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationMessage {
  private String correlationId;
  private String operation;
  private double a;
  private double b;
}
