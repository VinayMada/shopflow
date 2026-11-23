package com.shopflow;

import com.shopflow.dto.AuthResponse;
import com.shopflow.dto.LoginRequest;
import com.shopflow.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void register_shouldReturn200_withValidData() {
        RegisterRequest request = new RegisterRequest(uniqueEmail(), "secret123");

        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
    }

    @Test
    void login_shouldFail_withWrongPassword() {
        String email = uniqueEmail();
        restTemplate.postForEntity("/api/auth/register", new RegisterRequest(email, "secret123"), AuthResponse.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", new LoginRequest(email, "wrong-password"), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isFalse();
        assertThat(response.getBody()).contains("Invalid credentials");
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@shopflow.com";
    }
}
