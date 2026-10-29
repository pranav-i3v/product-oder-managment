package com.jforce.order.admin.service;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.repository.UserRepository;
import com.jforce.order.exception.user.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<JForceUser> authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public JForceUser register(String username, String email, String password) {

        String trimmedUsername = username.trim();

        if (userRepository.findByUsername(trimmedUsername).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        JForceUser user = new JForceUser();
        user.setUsername(trimmedUsername);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        JForceUser savedUser = userRepository.save(user);
        logger.info("User registration completed for {}", trimmedUsername);
        return savedUser;
    }
}
