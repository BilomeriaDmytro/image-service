package com.test.imageservice.security;

import com.test.imageservice.models.Account;
import com.test.imageservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Trying to load user with username - {}", username);
        Account account = accountRepository.findByUsername(username);

        if(account == null) {
            throw new UsernameNotFoundException("User not found");
        }

        log.info("User with username - {} successfully loaded", username);
        return new MyUserDetails(account);
    }
}