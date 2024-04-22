package org.evolve.repository;


import org.evolve.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findAllByUserId(Long userId);
}
