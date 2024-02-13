package org.evolve.repository;

import org.evolve.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    @Query("SELECT f FROM Friendship f WHERE f.userId = ?1 AND f.friendId = ?2")
    Optional<Friendship> findByUserFriendIds(Long userId, Long friendId);

    // TODO: Use constant
    @Query("SELECT f FROM Friendship f WHERE f.userId = ?1 OR f.friendId =?1 AND f.status = 'ACCEPTED'")
    List<Friendship> findFriends(Long userId);
}
