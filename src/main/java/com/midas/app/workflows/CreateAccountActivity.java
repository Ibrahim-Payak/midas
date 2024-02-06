package com.midas.app.workflows;

import com.midas.app.models.Account;
import com.stripe.exception.StripeException;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CreateAccountActivity {

  /**
   * createAccount create a new user account with Stripe Create Customer API
   *
   * @param details is the details of the account to be created.
   * @return User account created (status code 201)
   * @throws StripeException If an error occurs while interacting with the Stripe API.
   */
  @ActivityMethod
  Account createAccount(Account details) throws StripeException;
}
