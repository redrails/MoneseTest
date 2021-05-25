package com.ihtasham.monesetest.Domain;

import java.math.BigDecimal;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
  private Integer sender;
  private Integer recipient;
  private BigDecimal amount;
}
