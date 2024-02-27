package org.evolve.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
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
