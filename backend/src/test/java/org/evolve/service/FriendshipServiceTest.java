package org.evolve.service;

import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.Friendship;
import org.evolve.entity.FriendshipStatus;
import org.evolve.repository.FriendshipRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(FakerConfiguration.class)
public class FriendshipServiceTest {
  @Mock
  private FriendshipRepository friendshipRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private FriendshipService friendshipService;

  @Autowired
  private Faker faker;

  @Test
  public void givenFriendshipsExist_whenGetFriends_friendshipsFound() {
    Long currentUserId = faker.random().nextLong();
    Long friendId = faker.random().nextLong();
    Long friendId2 = faker.random().nextLong();

    when(userService.extractUserIdFromSecurityContext()).thenReturn(currentUserId);
    when(friendshipRepository.findFriends(currentUserId)).thenReturn(List.of(
      new Friendship(faker.random().nextLong(), currentUserId, friendId, FriendshipStatus.ACCEPTED),
      new Friendship(faker.random().nextLong(), friendId2, currentUserId, FriendshipStatus.ACCEPTED)
    ));

    List<Long> friends = friendshipService.getFriends();

    assertEquals(List.of(friendId, friendId2), friends);
    verify(userService, times(1)).extractUserIdFromSecurityContext();
    verify(friendshipRepository, times(1)).findFriends(any());
  }

  @Test
  public void givenExistingUsers_whenInvite_pendingFriendshipCreated() {
    Long currentUserId = faker.random().nextLong();
    Long friendId = faker.random().nextLong();

    when(userService.extractUserIdFromSecurityContext()).thenReturn(currentUserId);
    when(friendshipRepository.findByUserFriendIds(currentUserId, friendId))
      .thenReturn(Optional.of(new Friendship(
        faker.random().nextLong(), currentUserId,
        friendId, FriendshipStatus.PENDING)));

    when(friendshipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Friendship friendship = friendshipService.invite(friendId);

    assertNotNull(friendship);
    assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
    verify(userService, times(1)).extractUserIdFromSecurityContext();
    verify(friendshipRepository, times(1))
      .findByUserFriendIds(currentUserId, friendId);
    verify(friendshipRepository, times(1)).save(any());
  }

  @Test
  public void givenExistingRequest_whenAccept_thenFriendshipAccepted() {
    Long currentUserId = faker.random().nextLong();
    Long friendId = faker.random().nextLong();

    when(userService.extractUserIdFromSecurityContext()).thenReturn(currentUserId);
    when(friendshipRepository.findByUserFriendIds(currentUserId, friendId))
      .thenReturn(Optional.of(new Friendship(
        faker.random().nextLong(), friendId,
        currentUserId, FriendshipStatus.PENDING)));

    Friendship acceptedFriendship = friendshipService.accept(friendId);

    assertNotNull(acceptedFriendship);
    assertEquals(FriendshipStatus.ACCEPTED, acceptedFriendship.getStatus());
    verify(userService, times(1)).extractUserIdFromSecurityContext();
    verify(friendshipRepository, times(1))
      .findByUserFriendIds(currentUserId, friendId);
    verify(friendshipRepository, times(1)).save(any());
  }

  @Test
  public void givenExistingRequest_whenDecline_thenFriendshipDeclined() {
    Long currentUserId = faker.random().nextLong();
    Long friendId = faker.random().nextLong();

    when(userService.extractUserIdFromSecurityContext()).thenReturn(currentUserId);
    when(friendshipRepository.findByUserFriendIds(currentUserId, friendId))
      .thenReturn(Optional.of(new Friendship(
        faker.random().nextLong(), friendId,
        currentUserId, FriendshipStatus.PENDING)));

    Friendship declinedFriendship = friendshipService.decline(friendId);

    assertNotNull(declinedFriendship);
    assertEquals(FriendshipStatus.DECLINED, declinedFriendship.getStatus());
    verify(userService, times(1)).extractUserIdFromSecurityContext();
    verify(friendshipRepository, times(1))
      .findByUserFriendIds(currentUserId, friendId);
    verify(friendshipRepository, times(1)).save(any());
  }

  @Test
  public void givenExistingRequest_whenWithdrawInvite_thenFriendshipDeleted() {
    Long currentUserId = faker.random().nextLong();
    Long friendId = faker.random().nextLong();

    when(userService.extractUserIdFromSecurityContext()).thenReturn(currentUserId);
    when(friendshipRepository.findByUserFriendIds(currentUserId, friendId))
      .thenReturn(Optional.of(new Friendship(
        faker.random().nextLong(), currentUserId,
        friendId, FriendshipStatus.PENDING)));

    friendshipService.withdrawInvite(friendId);

    verify(userService, times(1)).extractUserIdFromSecurityContext();
    verify(friendshipRepository, times(1))
      .findByUserFriendIds(currentUserId, friendId);
    verify(friendshipRepository, times(1)).delete(any());
  }
}
