package org.evolve.service;

import lombok.RequiredArgsConstructor;
import org.evolve.entity.Friendship;
import org.evolve.entity.FriendshipStatus;
import org.evolve.repository.FriendshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    // TODO: Add to controller
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
                .orElse(Friendship.builder()
                        .userId(potentialFriendId)
                        .friendId(userId)
                        .status(FriendshipStatus.PENDING)
                        .build()
                );

        // TODO: Add check on the friendship existence?
        friendshipRepository.save(friendship);

        return friendship;
    }

    @Transactional
    public Friendship accept(Long potentialFriendId) {
        Long userId = userService.extractUserIdFromSecurityContext();

        Friendship friendship = friendshipRepository
                .findByUserFriendIds(userId, potentialFriendId)
                // TODO: Custom?
                .orElseThrow();

        // TODO: Add check on the friendship existence?
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        return friendship;
    }

    @Transactional
    public Friendship decline(Long potentialFriendId) {
        Long userId = userService.extractUserIdFromSecurityContext();

        Friendship friendship = friendshipRepository
                .findByUserFriendIds(userId, potentialFriendId)
                // TODO: Custom?
                .orElseThrow();

        // TODO: Add check on the friendship existence?
        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);

        return friendship;
    }

    @Transactional
    public Friendship withdrawInvite(Long potentialFriendId) {
        Long userId = userService.extractUserIdFromSecurityContext();

        Friendship friendship = friendshipRepository
                .findByUserFriendIds(userId, potentialFriendId)
                .orElse(Friendship.builder()
                        .userId(userId)
                        .friendId(potentialFriendId)
                        .status(FriendshipStatus.PENDING)
                        .build()
                );

        // TODO: Add check on the friendship existence?
        friendshipRepository.save(friendship);

        return friendship;
    }
}
