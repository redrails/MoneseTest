package com.ihtasham.monesetest.Controller;

import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Domain.CreateTransactionRequest;
import com.ihtasham.monesetest.Domain.MoneseApplicationResponse;
import com.ihtasham.monesetest.Domain.Transaction;
import com.ihtasham.monesetest.Exception.NoSuchAccountException;
import com.ihtasham.monesetest.Exception.NotEnoughBalanceException;
import com.ihtasham.monesetest.Repository.AccountRepository;
import com.ihtasham.monesetest.Repository.TransactionRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
  @Autowired private TransactionRepository transactionRepository;

  @Autowired private AccountRepository accountRepository;

  @RequestMapping(
      value = "/create",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MoneseApplicationResponse> create(
      @RequestBody CreateTransactionRequest request)
      throws NoSuchAccountException, NotEnoughBalanceException {
    Account sender =
        accountRepository.findById(request.getSender()).orElseThrow(NoSuchAccountException::new);
    Account recipient =
        accountRepository.findById(request.getRecipient()).orElseThrow(NoSuchAccountException::new);

    if (!hasEnoughBalance(sender, request.getAmount())) {
      throw new NotEnoughBalanceException();
    }

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

    return new ResponseEntity<>(
        MoneseApplicationResponse.builder().response("Transaction successfully completed").build(),
        HttpStatus.CREATED);
  }

  private boolean hasEnoughBalance(Account account, BigDecimal amount) {
    return account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) > 0;
  }

  @ExceptionHandler({NoSuchAccountException.class})
  private ResponseEntity<MoneseApplicationResponse> handleInvalidAccountException() {
    return new ResponseEntity<>(
        MoneseApplicationResponse.builder()
            .response("The specified account was not found, unable to complete transaction.")
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({NotEnoughBalanceException.class})
  private ResponseEntity<MoneseApplicationResponse> handleInvalidBalanceException() {
    return new ResponseEntity<>(
        MoneseApplicationResponse.builder()
            .response("Not enough balance to complete transaction")
            .build(),
        HttpStatus.BAD_REQUEST);
  }
}
