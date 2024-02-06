package com.midas.app.repositories;

import com.midas.app.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
  // custom query methods to find existing user with email
  Account findByEmail(String email);
}
