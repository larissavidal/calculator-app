package org.challenge.wit.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OperationMessage {
  private String correlationId;
  private String operation;
  private double a;
  private double b;
}
