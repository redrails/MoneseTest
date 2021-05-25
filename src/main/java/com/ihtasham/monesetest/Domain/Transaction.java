package com.ihtasham.monesetest.Domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JsonBackReference
    private Account sender;

    @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JsonBackReference
    private Account recipient;

    private BigDecimal amount;
}
