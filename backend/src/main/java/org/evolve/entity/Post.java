package org.evolve.entity;

import jakarta.persistence.*;
import lombok.*;
import org.evolve.dto.response.PostResponse;

@Entity
@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Post extends AuditableEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue
  private Integer id;

  @Column(name = "title", nullable = false)
  private String title;

  // todo discuss the content. What if we need more flexible solution? Not a simple text
  @Column(name = "content", nullable = false)
  private String content;

  @ManyToOne
  @JoinColumn
  private User user;

  public PostResponse toDto(Post post) {
    return new PostResponse(post.getId(), post.getTitle());
  }

}
