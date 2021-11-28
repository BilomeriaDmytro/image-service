package com.test.imageservice.presentation;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.dto.AccountAuthDTO;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.presentation.response.RegistrationResponse;
import com.test.imageservice.presentation.response.ResponseMessage;
import com.test.imageservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponse> registerAccount(@RequestBody AccountAuthDTO accountAuthDTO)
            throws InputException {

        String message = "";

        Account account = accountService.createNewAccount(accountAuthDTO);
        message = "Account successfully created!";

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new RegistrationResponse(message, account));
    }

    @DeleteMapping("/account/delete")
    public ResponseEntity<ResponseMessage> deleteAccount(){
        Account account = accountService.getCurrentUser();
        try {
            accountService.deleteAccount(account.getId());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage("Account is successfully deleted"));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Account is not found"));
        }
    }

    @PostMapping("/account/update/username")
    public ResponseEntity<RegistrationResponse> updateUsername(@RequestParam String username)
            throws NotFoundException, InputException {

        String message = "";
        Account account = accountService.getCurrentUser();
        accountService.updateUsername(account.getId(), username);

        message = "Username is successfully updated";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new RegistrationResponse(message, account));
    }

    @PostMapping("/account/update/password")
    public ResponseEntity<ResponseMessage> updatePassword(@RequestParam String password)
            throws NotFoundException, InputException {

        String message = "";
        Account account = accountService.getCurrentUser();
        Account updatedAccount = accountService.updatePassword(account.getId(), password);
        if(updatedAccount == null){
            message = "Password is too short";
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message));
        }

        message = "Password is successfully updated";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(message));
    }
}