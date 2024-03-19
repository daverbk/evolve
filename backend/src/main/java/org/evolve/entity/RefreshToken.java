package org.evolve.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "token", unique = true, nullable = false)
  private String token;

  @Column(name = "expiration_date", nullable = false)
  private Instant expirationDate;

  @OneToOne
  @JoinColumn(name = "user", referencedColumnName = "id")
  private User user;
}
