package com.test.imageservice.presentation.response;

import com.test.imageservice.models.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationResponse {

    private String message;

    private Long accountId;

    private String username;

    public RegistrationResponse(String message, Account account){

        this.message = message;
        this.accountId = account.getId();
        this.username = account.getUsername();
    }
}