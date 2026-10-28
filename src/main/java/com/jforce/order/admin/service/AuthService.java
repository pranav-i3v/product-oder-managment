package com.jforce.order.admin.service;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<JForceUser> authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Optional<JForceUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void createJForceUser(String username, String email, String password) {

        JForceUser user = new JForceUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

    }
}
