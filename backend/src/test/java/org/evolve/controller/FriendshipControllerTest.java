package org.evolve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.evolve.entity.Friendship;
import org.evolve.entity.FriendshipStatus;
import org.evolve.service.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class FriendshipControllerTest {
  @MockBean
  private FriendshipService friendshipService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  public void getFriendships() throws Exception {
    when(friendshipService.getFriends()).thenReturn(List.of(1L, 2L, 3L));
    mockMvc.perform(get("/friendships"))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(List.of(1L, 2L, 3L))));
    verify(friendshipService, times(1)).getFriends();
  }

  @Test
  @WithMockUser
  public void invite() throws Exception {
    Friendship friendship = new Friendship(1L, 1L, 2L, FriendshipStatus.PENDING);
    when(friendshipService.invite(2L)).thenReturn(friendship);
    mockMvc.perform(post("/friendships/invite/{id}", 2L))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(friendship)));
    verify(friendshipService, times(1)).invite(2L);
  }

  @Test
  @WithMockUser
  public void accept() throws Exception {
    Friendship friendship = new Friendship(1L, 1L, 2L, FriendshipStatus.ACCEPTED);
    when(friendshipService.accept(1L)).thenReturn(friendship);
    mockMvc.perform(post("/friendships/accept/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(friendship)));
    verify(friendshipService, times(1)).accept(1L);
  }

  @Test
  @WithMockUser
  public void decline() throws Exception {
    Friendship friendship = new Friendship(1L, 1L, 2L, FriendshipStatus.DECLINED);
    when(friendshipService.decline(1L)).thenReturn(friendship);
    mockMvc.perform(post("/friendships/decline/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(friendship)));
    verify(friendshipService, times(1)).decline(1L);
  }

  @Test
  @WithMockUser
  public void withdrawInvite() throws Exception {
    mockMvc.perform(post("/friendships/withdraw/{id}", 2L))
      .andExpect(status().isOk());
    verify(friendshipService, times(1)).withdrawInvite(2L);
  }
}
