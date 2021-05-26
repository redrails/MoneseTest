package com.ihtasham.monesetest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ihtasham.monesetest.Controller.AccountController;
import com.ihtasham.monesetest.Controller.TransactionController;
import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Domain.CreateTransactionRequest;
import com.ihtasham.monesetest.Domain.Transaction;
import com.ihtasham.monesetest.Exception.NoSuchAccountException;
import com.ihtasham.monesetest.Exception.NotEnoughBalanceException;
import com.ihtasham.monesetest.Repository.AccountRepository;
import com.ihtasham.monesetest.Repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class MoneseTestApplicationTests {

  @Mock AccountRepository accountRepository;

  @Mock TransactionRepository transactionRepository;

  @InjectMocks AccountController accountController;

  @InjectMocks TransactionController transactionController;

  @Captor ArgumentCaptor<Transaction> transactionArgumentCaptor;

  @Captor ArgumentCaptor<Account> accountArgumentCaptor;

  @Test
  void testAccountBalance() throws NoSuchAccountException {
    // Given
    Account account1 = Account.builder().id(1).balance(new BigDecimal(10)).build();

    given(accountRepository.findById(1)).willReturn(Optional.of(account1));

    // When

    ResponseEntity<Account> response = accountController.balance("1");

    // Then
    Assertions.assertEquals(response.getBody().getBalance(), new BigDecimal(10));
  }

  @Test
  void testInvalidAccountBalance() throws NoSuchAccountException {
    // When
    Assertions.assertThrows(
        NoSuchAccountException.class,
        // Then
        () -> accountController.balance("1"));
  }

  /**
   * Creating a valid transaction such that the sender DOES have sufficient balance to make a
   * transaction.
   */
  @Test
  void testValidTransaction() throws NoSuchAccountException, NotEnoughBalanceException {
    // Given
    Account account1 = Account.builder().id(1).balance(new BigDecimal(10)).build();
    Account account2 = Account.builder().id(2).balance(new BigDecimal(100)).build();

    given(accountRepository.findById(1)).willReturn(Optional.of(account1));
    given(accountRepository.findById(2)).willReturn(Optional.of(account2));

    CreateTransactionRequest createTransactionRequest =
        CreateTransactionRequest.builder()
            .sender(2)
            .recipient(1)
            .amount(new BigDecimal(10))
            .build();

    // When
    ResponseEntity response = transactionController.create(createTransactionRequest);

    // Then
    then(transactionRepository).should().save(transactionArgumentCaptor.capture());
    then(accountRepository).should(times(2)).save(accountArgumentCaptor.capture());

    Assertions.assertEquals(1, transactionArgumentCaptor.getAllValues().size());
    Assertions.assertEquals(new BigDecimal(10), transactionArgumentCaptor.getValue().getAmount());
    Assertions.assertEquals(
        new BigDecimal(90), accountArgumentCaptor.getAllValues().get(0).getBalance());
    Assertions.assertEquals(
        new BigDecimal(20), accountArgumentCaptor.getAllValues().get(1).getBalance());
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  /**
   * Creating an invalid transaction request such that the sender doesn't have enough balance to
   * make this transaction.
   */
  @Test
  void testTransactionWithInvalidBalance()
      throws NoSuchAccountException, NotEnoughBalanceException {
    // Given
    Account account1 = Account.builder().id(1).balance(new BigDecimal(0)).build();
    Account account2 = Account.builder().id(2).balance(new BigDecimal(10)).build();

    given(accountRepository.findById(1)).willReturn(Optional.of(account1));
    given(accountRepository.findById(2)).willReturn(Optional.of(account2));

    CreateTransactionRequest createTransactionRequest =
        CreateTransactionRequest.builder()
            .sender(1)
            .recipient(2)
            .amount(new BigDecimal(10))
            .build();

    // When
    Assertions.assertThrows(
        NotEnoughBalanceException.class,
        () -> transactionController.create(createTransactionRequest));

    // Then
    then(transactionRepository).should(times(0)).save(any());
    then(accountRepository).should(times(0)).save(any());
  }

  /**
   * Creating an invalid transaction request such that the sender doesn't have enough balance to
   * make this transaction.
   */
  @Test
  void testTransactionToInvalidAccount() throws NoSuchAccountException, NotEnoughBalanceException {
    // Given
    Account account1 = Account.builder().id(1).balance(new BigDecimal(0)).build();

    given(accountRepository.findById(1)).willReturn(Optional.of(account1));

    CreateTransactionRequest createTransactionRequest =
        CreateTransactionRequest.builder()
            .sender(1)
            .recipient(2)
            .amount(new BigDecimal(10))
            .build();

    // When
    Assertions.assertThrows(
        NoSuchAccountException.class, () -> transactionController.create(createTransactionRequest));

    // Then
    then(transactionRepository).should(times(0)).save(any());
    then(accountRepository).should(times(0)).save(any());
  }
}
