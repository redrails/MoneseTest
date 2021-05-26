package com.ihtasham.monesetest.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    return Stream.concat(
            sender_transactions.stream()
                .map(
                    t -> {
                      t.setAmount(t.getAmount().negate());
                      return t;
                    }),
            recipient_transactions.stream())
        .sorted(Comparator.comparing(Transaction::getCreatedOn).reversed())
        .collect(Collectors.toList());
  }
}
