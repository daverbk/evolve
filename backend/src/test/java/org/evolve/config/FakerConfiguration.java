package org.evolve.config;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FakerConfiguration {
  @Bean
  public Faker getFaker() {
    return new Faker();
  }
}
