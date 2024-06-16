package org.evolve.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.evolve.dto.request.CreatePostRequest;
import org.evolve.dto.response.PostResponse;
import org.evolve.dto.response.UserResponse;
import org.evolve.entity.Post;
import org.evolve.entity.User;
import org.evolve.service.PostService;
import org.evolve.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {
  private final UserService userService;
  private final PostService postService;

  @Operation(summary = "Get user by username")
  @GetMapping("/{username}")
  public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
      User user = userService.getByUsername(username);
      return new ResponseEntity<>(user.toResponseDto(user), HttpStatus.OK);
  }

  // todo add pagination
  @Operation(summary = "Get user posts")
  @GetMapping("/{username}/posts")
  public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable String username) {
    List<PostResponse> userPosts = postService.findUserPosts(username);
    return new ResponseEntity<>(userPosts, HttpStatus.OK);
  }

  @Operation(summary = "Create post")
  @PostMapping("/post")
  public ResponseEntity<PostResponse> createPost(@RequestBody @Valid CreatePostRequest createPostRequest) {
    return new ResponseEntity<>(postService.createPost(createPostRequest), HttpStatus.OK);
  }
}
