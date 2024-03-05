package org.evolve.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friendship")
@Schema(description = "Friendship connection")
public class Friendship {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  @Schema(description = "User who sent the friendship request", example = "1")
  private Long userId;

  @Column(name = "friend_id", nullable = false)
  @Schema(description = "Pending or accepted friendship user", example = "2")
  private Long friendId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Schema(description = "Friendship request status", example = "ACCEPTED")
  private FriendshipStatus status;
}
