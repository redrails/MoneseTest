package com.ihtasham.monesetest.Repository;

import com.ihtasham.monesetest.Domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {}
