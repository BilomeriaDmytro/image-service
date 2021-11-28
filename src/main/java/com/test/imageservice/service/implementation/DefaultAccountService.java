package com.test.imageservice.service.implementation;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.AccountStatus;
import com.test.imageservice.models.dto.AccountAuthDTO;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.repository.AccountRepository;
import com.test.imageservice.service.AccountService;
import com.test.imageservice.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class DefaultAccountService implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ImageService imageService;

    @Override
    public Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return accountRepository.findByUsername(authentication.getName());
    }

    @Override
    public Account createNewAccount(final AccountAuthDTO accountAuthDTO) throws InputException {
        log.info("Trying to register new account...");

        String message = "";
        final String ACCOUNT_CREATION_FAILED_WARN = "Failed to create account with username - '{}' Cause: {}";

        String username = accountAuthDTO.getUsername();
        if(accountRepository.findByUsername(username) != null){
            message = "Account with username - " + username + " already exists";
            log.warn(ACCOUNT_CREATION_FAILED_WARN, username, message);
            throw new InputException(message);
        }

        String newUsername = accountAuthDTO.getUsername();
        String newPassword = accountAuthDTO.getPassword();

        if(newUsername.length() < 4 & newPassword.length() < 4){
            message = "Username or password is too short, should be not less then 4 symbols";
            log.warn(ACCOUNT_CREATION_FAILED_WARN, username, message);
            throw new InputException(message);
        }

        Account account = new Account();
        account.setUsername(newUsername);
        account.setPassword(bCryptPasswordEncoder.encode(newPassword));
        account.setImages(new ArrayList<>());
        account.setStatus(AccountStatus.ACTIVE);
        Account savedAccount = accountRepository.save(account);

        log.info("New account successfully created");
        return savedAccount;
    }

    @Override
    @Transactional
    public void deleteAccount(Long id)
            throws Exception {

        final String DELETE_PROCESS_FAILED_WARN = "Deletion process failed for user with id - " + id + ". Cause: ";
        String message = "";

        Optional<Account> object = accountRepository.findById(id);
        if(object.isPresent()){
           Account account = object.get();
           account.setStatus(AccountStatus.DELETED);
           accountRepository.save(account);
           imageService.deleteAccountImages(account);
           log.info("Account with id - {} successfully deleted", account.getId());
        }else{
            message = "User with id " + id + " not found.";
            log.warn(DELETE_PROCESS_FAILED_WARN + message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Account updateUsername(Long id, String username)
            throws NotFoundException, InputException {

        final String UPDATE_PROCESS_FAILED_WARN = "Update process failed for user with id - " + id + ". Cause: ";
        String message = "";

        Optional<Account> object = accountRepository.findById(id);
        if(object.isPresent()){
            if (accountRepository.findByUsername(username) == null){
                if(username.length() > 3){
                    Account account = object.get();
                    account.setUsername(username);
                    log.info("Account with id - {} successfully updated", id);
                    return account;
                }
                message = "Username is too short, should be not less then 4 symbols.";
                log.warn(UPDATE_PROCESS_FAILED_WARN + message);
                throw new InputException(message);
            }
            message = "Username is taken.";
            log.warn(UPDATE_PROCESS_FAILED_WARN + message);
            throw new InputException(message);
        }
        message = "User with id " + id + " not found.";
        log.warn(UPDATE_PROCESS_FAILED_WARN + message);
        throw new NotFoundException(message);
    }

    @Override
    public Account updatePassword(Long id, String password)
            throws NotFoundException, InputException {

        final String UPDATE_PROCESS_FAILED_WARN = "Update process failed for user with id - " + id + ". Cause: ";
        String message = "";

        Optional<Account> object = accountRepository.findById(id);
        if(object.isPresent()){
            if(password.length() > 3){
                Account account = object.get();
                account.setPassword(bCryptPasswordEncoder.encode(password));
                log.info("Account with id - {} successfully updated", id);
                return account;
            }
            message = "Password is too short, should be not less then 4 symbols.";
            log.warn(UPDATE_PROCESS_FAILED_WARN + message);
            throw new InputException(message);
        }
        message = "User with id " + id + " not found.";
        log.warn(UPDATE_PROCESS_FAILED_WARN + message);
        throw new NotFoundException(message);
    }
}