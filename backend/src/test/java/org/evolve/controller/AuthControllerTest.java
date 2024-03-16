package org.evolve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.evolve.dto.request.SignInRequest;
import org.evolve.dto.request.SignUpRequest;
import org.evolve.dto.response.JwtAuthenticationResponse;
import org.evolve.service.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
  @MockBean
  private AuthenticationService authenticationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void signUp() throws Exception {
    SignUpRequest request = new SignUpRequest("username",
      "email@email.com", "password");
    when(authenticationService.signUp(request))
      .thenReturn(new ResponseEntity<>(
        "Email verification link has been sent to your email.", HttpStatus.OK));
    mockMvc.perform(post("/auth/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().string("Email verification link has been sent to your email."));
    verify(authenticationService, times(1)).signUp(request);
  }

  @Test
  public void signIn() throws Exception {
    SignInRequest request = new SignInRequest("username", "password");
    JwtAuthenticationResponse response = new JwtAuthenticationResponse(
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNjIwNn0.7",
      "550e8400-e29b-41d4-a716-446655440000", 3600, 7200);
    when(authenticationService.signIn(request)).thenReturn(response);
    mockMvc.perform(post("/auth/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));
    verify(authenticationService, times(1)).signIn(request);
  }
}
