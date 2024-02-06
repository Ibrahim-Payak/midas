package com.midas.app.workflows;

import com.midas.app.exceptions.InvalidRequestException;
import com.midas.app.exceptions.ResourceAlreadyExistsException;
import com.midas.app.models.Account;
import com.midas.app.models.AccountType;
import com.midas.app.repositories.AccountRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateAccountActivityImpl implements CreateAccountActivity {
  private final String stripeApiKey;

  @Autowired private AccountRepository accountRepository;

  public CreateAccountActivityImpl(String stripeApiKey) {
    this.stripeApiKey = stripeApiKey;
  }

  /**
   * createAccount create a new user account with Stripe Create Customer API
   *
   * @param details is the details of the account to be created.
   * @return User account created (status code 201)
   * @throws InvalidRequestException if any field of details is null.
   * @throws ResourceAlreadyExistsException if account with given email is already exist.
   * @throws StripeException If an error occurs while interacting with the Stripe API.
   */
  @Override
  public Account createAccount(Account details) {
    try {
      if (details.getEmail() == null
          || details.getFirstName() == null
          || details.getLastName() == null) {
        throw new InvalidRequestException("Email and firstName are required fields");
      }

      Stripe.apiKey = stripeApiKey;

      // Check if the account already exists
      //      Account existingAccount = accountRepository.findByEmail(details.getEmail());
      //      if (existingAccount != null) {
      //        // Account already exists, update updatedAt and save changes
      //        existingAccount.setUpdatedAt(OffsetDateTime.now());
      //        accountRepository.save(existingAccount);
      //        throw new ResourceAlreadyExistsException("Account already exists");
      //      }

      CustomerCreateParams params =
          CustomerCreateParams.builder()
              .setEmail(details.getEmail())
              .setName(details.getFirstName() + " " + details.getLastName())
              .build();
      Customer customer = Customer.create(params);

      // Convert customer data into form accepted by Account
      Instant instant = Instant.ofEpochSecond(customer.getCreated());
      // Create OffsetDateTime from Instant
      OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);

      // Map Customer object to Account Object
      Account createdAccount = new Account();
      createdAccount.setId(UUID.randomUUID());
      createdAccount.setFirstName(customer.getName().split(" ")[0]);
      createdAccount.setLastName(customer.getName().split(" ")[1]);
      createdAccount.setEmail(customer.getEmail());
      createdAccount.setCreatedAt(offsetDateTime);
      createdAccount.setUpdatedAt(OffsetDateTime.now());
      createdAccount.setProviderId(customer.getId());
      createdAccount.setProviderType(AccountType.STRIPE);

      return createdAccount;
    } catch (StripeException e) {
      // Handle Stripe API exceptions
      e.printStackTrace();
      return null;
    }
  }
}
