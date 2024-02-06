package org.evolve.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.evolve.entity.Role;
import org.evolve.entity.User;
import org.evolve.repositories.UserRepository;
import org.evolve.util.AppUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private JavaMailSender mailSender;

    /**
     * Save user
     *
     * @return saved user
     */
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * Create user
     */
    public void create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // TODO: Add custom exceptions
            throw new RuntimeException("User with this username already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        save(user);

        try {
            sendVerificationEmail(user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
            // TODO: Custom exception?
        }
    }

    public User getByUsername(String userName) {
        return repository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = getByUsername(userName);
        return new User(user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.getRole());
    }

    /**
     * Get user by username
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Get current user
     *
     * @return current user
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    /**
     * Give admin role to current user
     */
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }

    private void sendVerificationEmail(User user) throws
            MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "info@evolve.com";
        String senderName = "Evolve Inc";
        String subject = "Please verify your registration";
        String content =
                "Dear [[name]],<br/>"
                        + "Please click the link below to verify your registration:<br>"
                        + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                        + "Thank you,<br>"
                        + "Your company name.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = AppUtils.SITE_URL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }

    // TODO: Add javadoc
    public boolean verify(String verificationCode) {
        User user = repository.findByVerificationCode(verificationCode).orElseThrow(); // TODO: Add custom exception?

        if (user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            repository.save(user);

            return true;
        }

    }
}
