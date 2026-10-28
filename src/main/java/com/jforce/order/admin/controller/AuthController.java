package com.jforce.order.admin.controller;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

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
                        HttpSession session,
                        Model model) {

        Optional<JForceUser> user = authService.authenticateUser(username, password);

        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        session.setAttribute("user", user.get());

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

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (authService.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        authService.createJForceUser(username, email, password);

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
