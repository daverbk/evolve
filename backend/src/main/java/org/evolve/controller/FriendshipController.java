package org.evolve.controller;

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
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping
    public ResponseEntity<List<Long>> getFriendships() {
        return new ResponseEntity<>(friendshipService.getFriends(), HttpStatus.OK);
    }

    @PostMapping("/invite/{id}")
    public ResponseEntity<Friendship> invite(@PathVariable String id) {
        return new ResponseEntity<>(friendshipService.invite(Long.parseLong(id)), HttpStatus.OK);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<Friendship> accept(@PathVariable String id) {
        return new ResponseEntity<>(friendshipService.accept(Long.parseLong(id)), HttpStatus.OK);
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<Friendship> decline(@PathVariable String id) {
        return new ResponseEntity<>(friendshipService.decline(Long.parseLong(id)), HttpStatus.OK);
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity<?> withdrawInvite(@PathVariable String id) {
        friendshipService.withdrawInvite(Long.parseLong(id));
        return ResponseEntity.ok().build();
    }
}
