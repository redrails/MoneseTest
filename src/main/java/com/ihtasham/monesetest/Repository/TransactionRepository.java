package com.ihtasham.monesetest.Repository;

import com.ihtasham.monesetest.Domain.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {}
