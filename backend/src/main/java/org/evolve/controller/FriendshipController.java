package org.evolve.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.evolve.entity.Friendship;
import org.evolve.service.FriendshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/friendships/")
@Tag(name = "Friendships")
public class FriendshipController {
  private final FriendshipService friendshipService;

  @Operation(summary = "Get friends for the logged in user")
  @GetMapping
  public ResponseEntity<List<Long>> getFriendships() {
    return new ResponseEntity<>(friendshipService.getFriends(), HttpStatus.OK);
  }

  @Operation(summary = "Invite a user to be a friend")
  @PostMapping("/invite/{id}")
  public ResponseEntity<Friendship> invite(@PathVariable String id) {
    return new ResponseEntity<>(friendshipService.invite(Long.parseLong(id)), HttpStatus.OK);
  }

  @Operation(summary = "Accept a friend request")
  @PostMapping("/accept/{id}")
  public ResponseEntity<Friendship> accept(@PathVariable String id) {
    return new ResponseEntity<>(friendshipService.accept(Long.parseLong(id)), HttpStatus.OK);
  }

  @Operation(summary = "Decline a friend request")
  @PostMapping("/decline/{id}")
  public ResponseEntity<Friendship> decline(@PathVariable String id) {
    return new ResponseEntity<>(friendshipService.decline(Long.parseLong(id)), HttpStatus.OK);
  }

  @Operation(summary = "Withdraw a friend request")
  @PostMapping("/withdraw/{id}")
  public ResponseEntity<?> withdrawInvite(@PathVariable String id) {
    friendshipService.withdrawInvite(Long.parseLong(id));
    return ResponseEntity.ok().build();
  }
}
