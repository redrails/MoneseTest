package com.ihtasham.monesetest.Domain;

import lombok.*;

import java.math.BigDecimal;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateTransactionRequest {
    private Integer sender;
    private Integer recipient;
    private BigDecimal amount;
}
