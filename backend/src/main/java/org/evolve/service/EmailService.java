package org.evolve.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.evolve.entity.User;
import org.evolve.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailService {
  private final JavaMailSender emailSender;
  private final ResourceLoader resourceLoader;

  @Autowired
  public EmailService(@Qualifier("getMailSender") JavaMailSender emailSender,
                      ResourceLoader resourceLoader) {
    this.emailSender = emailSender;
    this.resourceLoader = resourceLoader;
  }

  public void sendVerificationEmail(User user)
    throws MessagingException, IOException {

    String verificationLink =
      AppConstants.APP_URL + "/auth/verify?code=" + user.getVerificationCode();

    String content = loadEmailTemplate(user.getUsername(), verificationLink);
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(AppConstants.EVOLVE_MAILBOX);
    helper.setTo(user.getEmail());
    helper.setSubject("Evolve: Email verification");
    helper.setText(content, true);
    emailSender.send(message);
  }

  private String loadEmailTemplate(String username, String verificationLink)
    throws IOException {

    Resource resource =
      resourceLoader.getResource("classpath:/templates/email_verification.html");

    String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    template = template
      .replace("[USERNAME]", username)
      .replaceAll("\\[VERIFICATION_LINK]", verificationLink);

    return template;
  }
}
