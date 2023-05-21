package com.example.devhouse.user_things.user;

import jakarta.servlet.http.*;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController {
    private final UserRepo  userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User user = userRepository.getByUsername(username);
        // Authenticate user using Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // User is already authenticated, return existing session ID
            return request.getSession().getId();
        }

        // Authenticate user with username and password
        // (You should replace this with your own authentication logic)
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Authentication successful, create session and return session ID
            HttpSession session = request.getSession(true);
            return session.getId();
        }  else {
            // Authentication failed, return error message
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid username or password";
        }
    }

    @GetMapping("/api/logout")
    public String customLogout(HttpServletRequest request, HttpServletResponse response) {
        // Get the Spring Authentication object of the current request.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // In case you are not filtering the users of this request URL.
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Invalidate the session
        request.getSession().invalidate();
        return "Logged out successfully";

    }

}
