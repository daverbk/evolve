package org.evolve.service;

import lombok.RequiredArgsConstructor;
import org.evolve.entity.Friendship;
import org.evolve.entity.FriendshipStatus;
import org.evolve.repository.FriendshipRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendshipService {
  private final FriendshipRepository friendshipRepository;
  private final UserService userService;

  public List<Long> getFriends() {
    Long userId = userService.extractUserIdFromSecurityContext();

    return friendshipRepository.findFriends(userId).stream()
      .flatMap(friendship -> Stream.of(friendship.getUserId(), friendship.getFriendId()))
      .filter(id -> !id.equals(userId))
      .distinct()
      .toList();
  }

  @Transactional
  public Friendship invite(Long potentialFriendId) {
    Long userId = userService.extractUserIdFromSecurityContext();

    Friendship friendship = friendshipRepository
      .findByUserFriendIds(userId, potentialFriendId)
      .orElseThrow(() -> new UsernameNotFoundException("Potential friend for invite is not found by id"));

    friendshipRepository.save(friendship);

    return friendship;
  }

  @Transactional
  public Friendship accept(Long potentialFriendId) {
    Long userId = userService.extractUserIdFromSecurityContext();

    Friendship friendship = friendshipRepository
      .findByUserFriendIds(userId, potentialFriendId)
      .orElseThrow(() -> new UsernameNotFoundException("Friend for friendship accept is not found by id"));

    friendship.setStatus(FriendshipStatus.ACCEPTED);
    friendshipRepository.save(friendship);

    return friendship;
  }

  @Transactional
  public Friendship decline(Long potentialFriendId) {
    Long userId = userService.extractUserIdFromSecurityContext();

    Friendship friendship = friendshipRepository
      .findByUserFriendIds(userId, potentialFriendId)
      .orElseThrow(() -> new UsernameNotFoundException("Friend for friendship decline is not found by id"));

    friendship.setStatus(FriendshipStatus.DECLINED);
    friendshipRepository.save(friendship);

    return friendship;
  }

  @Transactional
  public void withdrawInvite(Long potentialFriendId) {
    Long userId = userService.extractUserIdFromSecurityContext();

    Friendship friendship = friendshipRepository
      .findByUserFriendIds(userId, potentialFriendId)
      .orElseThrow(() -> new UsernameNotFoundException("Friend for friendship withdrawal is not found by id"));

    friendshipRepository.delete(friendship);
  }
}
