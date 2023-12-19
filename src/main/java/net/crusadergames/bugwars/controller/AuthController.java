package net.crusadergames.bugwars.controller;

import jakarta.validation.Valid;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.payload.request.LoginRequest;
import net.crusadergames.bugwars.payload.request.SignupRequest;
import net.crusadergames.bugwars.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }
}
