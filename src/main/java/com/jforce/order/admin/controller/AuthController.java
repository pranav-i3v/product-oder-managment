package com.jforce.order.admin.controller;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.service.AuthService;
import com.jforce.order.exception.user.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {

        Optional<JForceUser> user = authService.authenticateUser(username, password);

        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }
        request.getSession(true).setAttribute("user", user.get());

        if ("ADMIN".equals(user.get().getRole())) {
            return "redirect:/admin/products";
        }

        return "redirect:/products";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (username.isBlank()) {
            model.addAttribute("error", "Username is required");
            return "register";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            model.addAttribute("error", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {

            authService.register(username, email, password);
        } catch (UsernameAlreadyExistsException e) {

            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {

            logger.error("Error during registration for username {}: {}", username, e.getMessage());
            model.addAttribute("error", "An error occurred during registration, please try again later");
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
