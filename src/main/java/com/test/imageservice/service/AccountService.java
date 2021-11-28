package com.test.imageservice.service;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.dto.AccountAuthDTO;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;

public interface AccountService {

    Account getCurrentUser();

    Account createNewAccount(AccountAuthDTO accountAuthDTO) throws InputException;

    void deleteAccount(Long id) throws Exception;

    Account updateUsername(Long id, String username) throws NotFoundException, InputException;

    Account updatePassword(Long id, String password) throws NotFoundException, InputException;
}