package org.evolve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.Friendship;
import org.evolve.entity.FriendshipStatus;
import org.evolve.service.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(FakerConfiguration.class)
class FriendshipControllerTest {
  @MockBean
  private FriendshipService friendshipService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Faker faker;

  @Test
  @WithMockUser
  public void givenCorrectRequest_whenGetFriendships_thenSuccessful() throws Exception {
    List<Long> response = faker
      .collection(() -> faker.random().nextLong())
      .maxLen(3)
      .generate();

    when(friendshipService.getFriends()).thenReturn(response);

    mockMvc.perform(get("/friendships"))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(friendshipService, times(1)).getFriends();
  }

  @Test
  @WithMockUser
  public void givenCorrectRequest_whenInvite_thenSuccessful() throws Exception {
    Long userId = faker.random().nextLong(1, 9);
    Long friendId = faker.random().nextLong(10, 20);
    Friendship response = new Friendship(userId, userId, friendId, FriendshipStatus.PENDING);

    when(friendshipService.invite(friendId)).thenReturn(response);

    mockMvc.perform(post("/friendships/invite/{id}", friendId))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(friendshipService, times(1)).invite(friendId);
  }

  @Test
  @WithMockUser
  public void givenCorrectRequest_whenAccept_thenSuccessful() throws Exception {
    Long userId = faker.random().nextLong(1, 9);
    Long friendId = faker.random().nextLong(10, 20);
    Friendship response = new Friendship(userId, userId, friendId, FriendshipStatus.ACCEPTED);

    when(friendshipService.accept(userId)).thenReturn(response);

    mockMvc.perform(post("/friendships/accept/{id}", userId))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(friendshipService, times(1)).accept(userId);
  }

  @Test
  @WithMockUser
  public void givenCorrectRequest_whenDecline_thenSuccessful() throws Exception {
    Long userId = faker.random().nextLong(1, 9);
    Long friendId = faker.random().nextLong(10, 20);
    Friendship response = new Friendship(userId, userId, friendId, FriendshipStatus.DECLINED);

    when(friendshipService.decline(userId)).thenReturn(response);

    mockMvc.perform(post("/friendships/decline/{id}", userId))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(friendshipService, times(1)).decline(userId);
  }

  @Test
  @WithMockUser
  public void givenCorrectRequest_whenWithdrawInvite_thenSuccessful() throws Exception {
    Long friendId = faker.random().nextLong();

    mockMvc.perform(post("/friendships/withdraw/{id}", friendId))
      .andExpect(status().isOk());

    verify(friendshipService, times(1)).withdrawInvite(friendId);
  }
}
