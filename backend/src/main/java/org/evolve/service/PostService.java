package org.evolve.service;

import lombok.AllArgsConstructor;
import org.evolve.dto.request.CreatePostRequest;
import org.evolve.dto.response.PostResponse;
import org.evolve.entity.Post;
import org.evolve.entity.User;
import org.evolve.repository.PostRepository;
import org.evolve.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  public Post getPostById(Integer id) {
    return postRepository.findById(id)
      .orElseThrow(() -> new NoSuchElementException("Post not found"));
  }

  public List<PostResponse> findUserPosts(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    List<Post> posts = postRepository.findAllByUserId(user.getId());

    if (!posts.isEmpty()) {
      return listToPostDto(posts);
    }

    throw new NoSuchElementException("No posts yet");
  }

  @Transactional
  public PostResponse createPost(CreatePostRequest createPostRequest) {
    Post post = new Post();
    Optional<User> user = userRepository.findById(userService.extractUserIdFromSecurityContext());

    if (user.isEmpty()) {
      throw new UsernameNotFoundException("Cant create post for this user. Try again.");
    }

    post.setContent(createPostRequest.getContent());
    post.setTitle(createPostRequest.getTitle());
    post.setUser(user.get());

    postRepository.save(post);

    return post.toDto(post);
  }

  public List<PostResponse> listToPostDto(List<Post> plainPostsList) {
    List<PostResponse> postsDto = new ArrayList<>();
    plainPostsList.forEach(plain -> postsDto.add(plain.toDto(plain)));
    return postsDto;
  }
}
