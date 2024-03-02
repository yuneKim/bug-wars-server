package net.crusadergames.bugwars.controller;

import jakarta.validation.Valid;
import net.crusadergames.bugwars.dto.request.LoginDTO;
import net.crusadergames.bugwars.dto.request.SignupDTO;
import net.crusadergames.bugwars.dto.request.TokenRefreshRequestDTO;
import net.crusadergames.bugwars.dto.response.JwtDTO;
import net.crusadergames.bugwars.dto.response.TokenRefreshResponseDTO;
import net.crusadergames.bugwars.exception.RefreshTokenException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.dto.request.UpdateProfileRequestDTO;
import net.crusadergames.bugwars.dto.response.UserProfileResponseDTO;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody SignupDTO signUpDTO) {
        return authService.registerUser(signUpDTO);
    }

    @PostMapping("/login")
    public JwtDTO authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        return authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/refresh-token")
    public TokenRefreshResponseDTO refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        try {
            return authService.refreshToken(request.getRefreshToken());
        } catch (RefreshTokenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token is invalid.");
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(Principal principal) {
        if (principal == null) return;

        try {
            authService.logout(principal.getName());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/update-profile")
    public User updateProfile(@Valid @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO, Principal principal) {
        return authService.updateUserProfile(principal.getName(), updateProfileRequestDTO);
    }

    @GetMapping("/user-profile")
    public UserProfileResponseDTO getUserProfile(Principal principal) {
        return authService.getUserProfile(principal.getName());
    }

    @PostMapping("/verify/{username}/{token}")
    public boolean verifyEmail(@PathVariable String username, @PathVariable String token) {
        return authService.verifyEmailToken(token, username);
    }
}
