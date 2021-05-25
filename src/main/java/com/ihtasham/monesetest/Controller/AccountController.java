package com.ihtasham.monesetest.Controller;

import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Repository.AccountRepository;
import com.ihtasham.monesetest.Repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
  @Autowired private AccountRepository accountRepository;

  @Autowired private TransactionRepository transactionRepository;

  @RequestMapping(
      value = "/balance",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Account balance(@RequestParam(value = "id") String id) {
    return accountRepository.findById(Integer.valueOf(id)).orElseThrow();
  }

  @PutMapping("/create")
  public HttpStatus create(@RequestBody Map<String, Object> request) {
    Account account =
        Account.builder().balance(new BigDecimal((String) request.get("balance"))).build();
    accountRepository.save(account);
    return HttpStatus.CREATED;
  }
}
