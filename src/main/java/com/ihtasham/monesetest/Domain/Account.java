package com.ihtasham.monesetest.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"sender_transactions", "recipient_transactions"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private BigDecimal balance;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sender")
    @JsonManagedReference
    private List<Transaction> sender_transactions;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipient")
    @JsonManagedReference
    private List<Transaction> recipient_transactions;

    public List<Transaction> getTransactions() {
        return Stream.concat(sender_transactions.stream(), recipient_transactions.stream()).collect(Collectors.toList());
    }
}