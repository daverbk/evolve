package org.evolve.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.datafaker.Faker;
import org.evolve.config.FakerConfiguration;
import org.evolve.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(FakerConfiguration.class)
public class EmailServiceTest {
  @Mock
  private JavaMailSender emailSender;

  @Mock
  private ResourceLoader resourceLoader;

  @InjectMocks
  private EmailService emailService;

  @Autowired
  private Faker faker;

  @Test
  public void givenCorrectContent_whenSendVerificationEmail_thenNoException()
    throws MessagingException, IOException {

    User user = new User();
    user.setEmail(faker.internet().emailAddress());
    user.setUsername(faker.internet().username());
    user.setVerificationCode(faker.internet().uuid());

    String content = "Sample email content";
    Resource resource = mock(Resource.class);
    MimeMessage mimeMessage = mock(MimeMessage.class);

    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(resourceLoader.getResource(any())).thenReturn(resource);
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes()));
    doNothing().when(emailSender).send(mimeMessage);

    emailService.sendVerificationEmail(user);

    verify(emailSender, times(1)).createMimeMessage();
  }
}
