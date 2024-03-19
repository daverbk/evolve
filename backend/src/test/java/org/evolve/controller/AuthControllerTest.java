package org.evolve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.dto.request.SignInRequest;
import org.evolve.dto.request.SignUpRequest;
import org.evolve.dto.response.JwtAuthenticationResponse;
import org.evolve.service.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
@Import(FakerConfiguration.class)
class AuthControllerTest {
  @MockBean
  private AuthenticationService authenticationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Faker faker;

  @Test
  public void givenCorrectRequest_whenSignUp_thenSuccessful() throws Exception {
    SignUpRequest request = new SignUpRequest(faker.internet().username(),
      faker.internet().emailAddress(), faker.internet().password());
    String response = "Email verification link has been sent to your email.";

    when(authenticationService.signUp(request))
      .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

    mockMvc.perform(post("/auth/sign-up")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().string(response));

    verify(authenticationService, times(1)).signUp(request);
  }

  @Test
  public void givenCorrectRequest_whenSignIn_thenSuccessful() throws Exception {
    SignInRequest request = new SignInRequest(
      faker.internet().username(), faker.internet().password());

    JwtAuthenticationResponse response = new JwtAuthenticationResponse(
      faker.letterify("????????????????????????????????????????????????0.7"),
      faker.internet().uuid(), 3600, 7200);

    when(authenticationService.signIn(request)).thenReturn(response);

    mockMvc.perform(post("/auth/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(authenticationService, times(1)).signIn(request);
  }
}
