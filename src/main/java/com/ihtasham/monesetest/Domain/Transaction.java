package com.ihtasham.monesetest.Domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/London")
  private Date createdOn;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonBackReference
  private Account sender;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonBackReference
  private Account recipient;

  private BigDecimal amount;
}
