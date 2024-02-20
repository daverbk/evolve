package org.evolve.service;

import lombok.AllArgsConstructor;
import org.evolve.entity.User;
import org.evolve.exception.auth.UserAlreadyExistsException;
import org.evolve.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getByUsername(String userName) {
        return repository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

    public Long extractUserIdFromSecurityContext() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @Transactional
    public void create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername(), "User with this username already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail(), "User with this email already exists");
        }

        save(user);
    }

    @Transactional
    public void save(User user) {
        repository.save(user);
    }

    @Transactional
    public boolean verify(String verificationCode) {
        User user = repository.findByVerificationCode(verificationCode).orElseThrow();

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
