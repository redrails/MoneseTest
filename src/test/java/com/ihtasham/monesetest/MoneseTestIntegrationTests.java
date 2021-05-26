package com.ihtasham.monesetest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ihtasham.monesetest.Domain.Account;
import com.ihtasham.monesetest.Repository.AccountRepository;
import com.ihtasham.monesetest.Repository.TransactionRepository;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = MoneseTestApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class MoneseTestIntegrationTests {
  @Autowired AccountRepository accountRepository;

  @Autowired TransactionRepository transactionRepository;

  @Autowired private MockMvc mockMvc;

  @Test
  public void endToEndTest() throws Exception {
    // Create an account
    mockMvc
        .perform(
            put("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    String.valueOf(
                        (JsonNodeFactory.instance.objectNode().put("balance", "10.00")))))
        .andExpect(status().isCreated());

    // Create another account
    mockMvc
        .perform(
            put("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    String.valueOf(
                        (JsonNodeFactory.instance.objectNode().put("balance", "100.00")))))
        .andExpect(status().isCreated());

    // Make a transaction
    mockMvc
        .perform(
            put("/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransactionPayload(1, 2, new BigDecimal(0.55))))
        .andExpect(status().isCreated());

    // Make another transaction
    mockMvc
        .perform(
            put("/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransactionPayload(1, 2, new BigDecimal(3.24))))
        .andExpect(status().isCreated());

    // Make another transaction
    mockMvc
        .perform(
            put("/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransactionPayload(2, 1, new BigDecimal(100.00))))
        .andExpect(status().isCreated());

    // Make another transaction - invalid
    mockMvc
        .perform(
            put("/transaction/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createTransactionPayload(2, 1, new BigDecimal(1000.00))))
        .andExpect(status().isBadRequest());

    Account account1 = accountRepository.findById(1).orElseThrow();
    Account account2 = accountRepository.findById(2).orElseThrow();
    Assertions.assertEquals(new BigDecimal("106.21"), account1.getBalance());
    Assertions.assertEquals(new BigDecimal("3.79"), account2.getBalance());
  }

  private String createTransactionPayload(Integer sender, Integer recipient, BigDecimal amount) {
    ObjectNode payload = JsonNodeFactory.instance.objectNode();
    payload.put("sender", sender);
    payload.put("recipient", recipient);
    payload.put("amount", amount);
    return String.valueOf(payload);
  }
}
