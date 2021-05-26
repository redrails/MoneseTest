package com.ihtasham.monesetest.Controller;

import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Domain.MoneseApplicationResponse;
import com.ihtasham.monesetest.Exception.NoSuchAccountException;
import com.ihtasham.monesetest.Repository.AccountRepository;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
  @Autowired private AccountRepository accountRepository;

  @RequestMapping(
      value = "/balance",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Account> balance(@RequestParam(value = "id") String id)
      throws NoSuchAccountException {
    return new ResponseEntity<>(
        accountRepository.findById(Integer.valueOf(id)).orElseThrow(NoSuchAccountException::new),
        HttpStatus.OK);
  }

  @PutMapping("/create")
  public ResponseEntity<MoneseApplicationResponse> create(
      @RequestBody Map<String, Object> request) {
    accountRepository.save(
        Account.builder().balance(new BigDecimal((String) request.get("balance"))).build());
    return new ResponseEntity<>(
        MoneseApplicationResponse.builder().response("Successfully created a new account.").build(),
        HttpStatus.CREATED);
  }

  @ExceptionHandler({NoSuchAccountException.class})
  private ResponseEntity<MoneseApplicationResponse> handleInvalidAccountException() {
    return new ResponseEntity<MoneseApplicationResponse>(
        MoneseApplicationResponse.builder()
            .response("The specified account was not found, unable to get account details.")
            .build(),
        HttpStatus.BAD_REQUEST);
  }
}
