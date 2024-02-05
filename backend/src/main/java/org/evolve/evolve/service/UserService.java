package org.evolve.evolve.service;

import lombok.RequiredArgsConstructor;
import org.evolve.evolve.entity.Role;
import org.evolve.evolve.entity.User;
import org.evolve.evolve.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

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
     *
     * @return created user
     */
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // TODO: Add custom exceptions
            throw new RuntimeException("User with this username already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        return save(user);
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
}
