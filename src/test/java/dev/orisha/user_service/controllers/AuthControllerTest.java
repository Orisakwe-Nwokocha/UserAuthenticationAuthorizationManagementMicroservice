package dev.orisha.user_service.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orisha.user_service.dto.UserDTO;
import dev.orisha.user_service.dto.requests.LoginRequest;
import dev.orisha.user_service.dto.requests.RegisterRequest;
import dev.orisha.user_service.dto.requests.UserUpdateRequest;
import dev.orisha.user_service.dto.responses.ApiResponse;
import dev.orisha.user_service.dto.responses.LoginResponse;
import dev.orisha.user_service.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static dev.orisha.user_service.data.enums.Authority.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/db/data.sql"})
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BLACKLISTED_TOKEN = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJvcmlzaGEuZGV2IiwiaWF0IjoxNzIzMzk0Mjk5LCJleHAiOjE3MjM0ODA2OTksInN1YiI6InVzZXIiLCJwcmluY2lwYWwiOiJ1c2VyIiwiY3JlZGVudGlhbHMiOiJbUFJPVEVDVEVEXSIsImF1dGhvcml0aWVzIjpbIlVTRVIiXX0.E-wHrx_7sp2xSloSMoVuVCNY5OdZ6Wh80BomoSAH8XSWSSrD8WB52EInr6Pc6HQKc6ZLzegGY7kDbqxV3ipwtQ";

    @Test
    void registerUserTest() throws Exception {
        RegisterRequest request = buildRegisterRequest();
        byte[] content = new ObjectMapper().writeValueAsBytes(request);
        mockMvc.perform(post("/users/api/v1/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andDo(print());

    }

    @Test
    void updateUserTest() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("user");
        request.setPassword("password");
        UserDTO user = userService.getUserDTO(request.getEmail());
        int size = user.getAuthorities().size();
        assertEquals(1, size);

        request.setAuthority(ADMIN);
        UserDTO update = userService.update(request);
        assertNotNull(update);

        user = userService.getUserDTO(request.getEmail());
        size = user.getAuthorities().size();
        assertEquals(2, size);
    }

    @Test
    public void authenticateUserTest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user");
        request.setPassword("password");
        byte[] content = new ObjectMapper().writeValueAsBytes(request);
        mockMvc.perform(post("/users/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void authenticateUserTest_FailsForInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user");
        request.setPassword("wrongPassword");
        byte[] content = new ObjectMapper().writeValueAsBytes(request);
        mockMvc.perform(post("/users/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test

    public void testUserControllerForAuthenticatedUsers() throws Exception {
        String token = getToken();
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello admin!"))
                .andDo(print());
    }

    @Test
    public void logoutUserTest() throws Exception {
        String token = getToken();
        mockMvc.perform(delete("/users/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testThatBlacklistedTokenCannotBeAuthorized() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + BLACKLISTED_TOKEN))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    private String getToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin");
        request.setPassword("password");
        byte[] content = objectMapper.writeValueAsBytes(request);
        MvcResult result = mockMvc.perform(post("/users/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();
        TypeReference<ApiResponse<LoginResponse>> typeReference = new TypeReference<>() {};
        ApiResponse<LoginResponse> response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), typeReference);
        LoginResponse loginResponse = response.getData();
        return loginResponse.getToken();
    }

    public static RegisterRequest buildRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("username");
        request.setPassword("password");
        return request;
    }
}