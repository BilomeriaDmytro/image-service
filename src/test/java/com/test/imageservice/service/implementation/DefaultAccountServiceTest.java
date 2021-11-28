package com.test.imageservice.service.implementation;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.AccountStatus;
import com.test.imageservice.models.dto.AccountAuthDTO;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.repository.AccountRepository;
import com.test.imageservice.service.AccountService;
import com.test.imageservice.service.ImageService;
import org.junit.Assert;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.xmlunit.builder.Input;

import javax.persistence.criteria.CriteriaBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class DefaultAccountServiceTest {

    @Autowired
    @InjectMocks
    private DefaultAccountService defaultAccountService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void createNewAccount() throws InputException {

        when(accountRepository.save(any(Account.class))).thenAnswer(returnsFirstArg());

        AccountAuthDTO accountAuthDTO = new AccountAuthDTO("username", "password");
        Account account = defaultAccountService.createNewAccount(accountAuthDTO);

        assertEquals(account.getUsername(), accountAuthDTO.getUsername());
        assertEquals(account.getStatus(), AccountStatus.ACTIVE);
        assertNotNull(account.getImages());
    }

    @Test
    void UpdateUsernameSuccess() throws InputException, NotFoundException {

        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Account()));
        String username = "newUsername";

        Account account = defaultAccountService.updateUsername(1L,username);
        assertEquals(account.getUsername(), username);
    }

    @Test
    void updateUsernameFailure_TooShort() {

        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Account()));
        String username = "";

        Exception exception = assertThrows(InputException.class, () -> {
            defaultAccountService.updateUsername(1L,username);
        });

        assertEquals(exception.getMessage(), "Username is too short, should be not less then 4 symbols.");
    }

    @Test
    void updateUsernameFailure_UsernameTaken() {
        String username = "username";
        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Account()));
        when(accountRepository.findByUsername(username)).thenReturn(new Account());

        Exception exception = assertThrows(InputException.class, () -> {
            defaultAccountService.updateUsername(1L,username);
        });

        assertEquals(exception.getMessage(), "Username is taken.");
    }

    @Test
    void updatePasswordSuccess() throws InputException, NotFoundException {

        Account account = new Account();
        account.setPassword("oldPassword");
        account.setId(1L);

        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(account));
        when(bCryptPasswordEncoder.encode(anyString())).thenAnswer(returnsFirstArg());

        String newPassword = "newPassword";
        Account updatedAccount = defaultAccountService.updatePassword(1L, newPassword);
        assertEquals(updatedAccount.getPassword(), newPassword);
    }

    @Test
    void updatePasswordFailure_TooShort() {

        when(accountRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Account()));
        String password = "";

        Exception exception = assertThrows(InputException.class, () -> {
            defaultAccountService.updatePassword(1L,password);
        });

        assertEquals(exception.getMessage(), "Password is too short, should be not less then 4 symbols.");
    }

    @Test
    void updatePasswordFailure_NotFound() {

        String password = "username";

        Exception exception = assertThrows(NotFoundException.class, () -> {
            defaultAccountService.updateUsername(1L,password);
        });

        assertEquals(exception.getMessage(), "User with id " + 1 + " not found.");
    }
}