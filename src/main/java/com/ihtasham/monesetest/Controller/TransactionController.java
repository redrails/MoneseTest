package com.ihtasham.monesetest.Controller;

import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Domain.CreateTransactionRequest;
import com.ihtasham.monesetest.Domain.Transaction;
import com.ihtasham.monesetest.Repository.AccountRepository;
import com.ihtasham.monesetest.Repository.TransactionRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
  @Autowired private TransactionRepository transactionRepository;

  @Autowired private AccountRepository accountRepository;

  @PutMapping("/create")
  public HttpStatus create(@RequestBody CreateTransactionRequest request) {
    Account sender = accountRepository.findById(request.getSender()).orElseThrow();
    Account recipient = accountRepository.findById(request.getRecipient()).orElseThrow();
    if (hasEnoughBalance(sender, request.getAmount())) {
      Transaction transaction =
          Transaction.builder()
              .sender(sender)
              .recipient(recipient)
              .amount(request.getAmount())
              .build();
      sender.setBalance(sender.getBalance().subtract(request.getAmount()));
      recipient.setBalance(recipient.getBalance().add(request.getAmount()));
      transactionRepository.save(transaction);
      accountRepository.save(sender);
      accountRepository.save(recipient);
      return HttpStatus.CREATED;
    } else {
      return HttpStatus.BAD_REQUEST;
    }
  }

  private boolean hasEnoughBalance(Account account, BigDecimal amount) {
    return account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) > 0;
  }
}
