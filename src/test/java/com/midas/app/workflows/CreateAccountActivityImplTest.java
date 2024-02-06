package com.midas.app.workflows;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.midas.app.exceptions.*;
import com.midas.app.models.Account;
import com.midas.app.models.AccountType;
import com.midas.app.repositories.AccountRepository;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CreateAccountActivityImplTest {

  @Mock private AccountRepository accountRepository;

  @InjectMocks private CreateAccountActivityImpl createAccountActivity;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void createAccount_Success() throws StripeException {
    String email = "ibu@gmail.com";
    String firstName = "Ibu";
    String lastName = "Payak";

    Account details = new Account();
    details.setEmail(email);
    details.setFirstName(firstName);
    details.setLastName(lastName);

    // Test the createAccount method
    Account createdAccount = createAccountActivity.createAccount(details);

    // Verify the behavior
    assertNotNull(createdAccount);
    assertEquals(email, createdAccount.getEmail());
    assertEquals(firstName, createdAccount.getFirstName());
    assertEquals(lastName, createdAccount.getLastName());
    assertEquals(AccountType.STRIPE, createdAccount.getProviderType());
  }

  @Test
  public void createAccount_AccountExists() throws StripeException {
    String email = "ibu@gmail.com";
    String firstName = "Ibu";
    String lastName = "Payak";

    Account details = new Account();
    details.setEmail(email);
    details.setFirstName(firstName);
    details.setLastName(lastName);

    // Mocking repository behavior
    when(accountRepository.findByEmail(email)).thenReturn(new Account());

    // Test the createAccount method
    assertThrows(
        ResourceAlreadyExistsException.class, () -> createAccountActivity.createAccount(details));
  }

  @Test
  public void createAccount_MissingRequiredFields() {
    // Test with missing email
    Account details1 = new Account();
    details1.setFirstName("Ibu");
    details1.setLastName("Payak");
    assertThrows(
        InvalidRequestException.class, () -> createAccountActivity.createAccount(details1));

    // Test with missing firstName
    Account details2 = new Account();
    details2.setEmail("ibu@gmail.com");
    details2.setLastName("Payak");
    assertThrows(
        InvalidRequestException.class, () -> createAccountActivity.createAccount(details2));

    // Test with missing lastName
    Account details3 = new Account();
    details3.setEmail("ibu@gmail.com");
    details3.setFirstName("Ibu");
    assertThrows(
        InvalidRequestException.class, () -> createAccountActivity.createAccount(details3));
  }
}
