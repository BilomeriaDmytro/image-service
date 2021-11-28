package com.test.imageservice.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.test.imageservice.models.dto.AccountAuthDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = ("/application-test.properties"))
@Sql(value = {"/sql/create-instances-before-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/delete-instances-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAccountSuccess() throws Exception {

        AccountAuthDTO accountAuthDTO = new AccountAuthDTO("testuser","test");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(accountAuthDTO);

        this.mockMvc.perform(post("/signup")
                        .contentType("application/json; charset=utf8")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account successfully created!")));
    }

    @Test
    void registerAccountCredentialsFailure()
            throws Exception {

        AccountAuthDTO accountAuthDTO = new AccountAuthDTO("","");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(accountAuthDTO);

        this.mockMvc.perform(post("/signup")
                        .contentType("application/json; charset=utf8")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username or password is too short")));
    }

    @Test
    void registerAccountCredentialsFailure_UsernameIsTaken()
            throws Exception {

        AccountAuthDTO accountAuthDTO = new AccountAuthDTO("testuser1","test");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(accountAuthDTO);

        this.mockMvc.perform(post("/signup")
                        .contentType("application/json; charset=utf8")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("already exists")));
    }

    @Test
    void deleteAccount()
            throws Exception {

        this.mockMvc.perform(delete("/account/delete")
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account is successfully deleted")));
    }

    @Test
    void updateUsernameSuccess()
            throws Exception {

        String newUsername = "newTestUser";

        this.mockMvc.perform(post("/account/update/username?username=" + newUsername)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Username is successfully updated")));
    }

    @Test
    void updateUsernameFailure_IsTaken()
            throws Exception {

        String newUsername = "testuser2";

        this.mockMvc.perform(post("/account/update/username?username=" + newUsername)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username is taken")));
    }

    @Test
    void updateUsernameFailure_tooShort()
            throws Exception {

        String newUsername = "";

        this.mockMvc.perform(post("/account/update/username?username=" + newUsername)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username is too short")));
    }

    @Test
    void updatePasswordSuccess()
            throws Exception {

        String newPassword = "newPassword";

        this.mockMvc.perform(post("/account/update/password?password=" + newPassword)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password is successfully updated")));
    }

    @Test
    void updatePasswordFailure_tooShort()
            throws Exception {

        String newPassword = "";

        this.mockMvc.perform(post("/account/update/password?password=" + newPassword)
                        .with(httpBasic("testuser1","test"))
                        .contentType("application/json; charset=utf8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password is too short")));
    }
}