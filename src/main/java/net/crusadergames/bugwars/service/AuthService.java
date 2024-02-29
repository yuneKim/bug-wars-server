package net.crusadergames.bugwars.service;

import com.modernmt.text.profanity.ProfanityFilter;
import net.crusadergames.bugwars.dto.request.LoginRequest;
import net.crusadergames.bugwars.dto.request.SignupRequest;
import net.crusadergames.bugwars.dto.request.TokenRefreshRequest;
import net.crusadergames.bugwars.dto.request.UpdateProfileRequest;
import net.crusadergames.bugwars.dto.response.JwtResponse;
import net.crusadergames.bugwars.dto.response.TokenRefreshResponse;
import net.crusadergames.bugwars.dto.response.UserProfileResponse;
import net.crusadergames.bugwars.exception.TokenRefreshException;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.RefreshToken;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.RoleRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.security.jwt.JwtUtils;
import net.crusadergames.bugwars.security.service.RefreshTokenService;
import net.crusadergames.bugwars.security.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    public User registerUser(SignupRequest signUpRequest) {

        ProfanityFilter profanityFilter = new ProfanityFilter();

        if (userRepository.existsByUsernameIgnoreCase(signUpRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        if (userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }

        if (profanityFilter.test("en", signUpRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inappropriate language.");
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        // Set profile name to be equal to username initially
        user.setProfileName(signUpRequest.getUsername());

        Optional<Role> optUserRole = roleRepository.findByName(ERole.ROLE_USER);
        if (optUserRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
        }
        Role userRole = optUserRole.get();
        user.setRoles(Set.of(userRole));
        return userRepository.save(user);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), roles);
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken).orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                "Refresh token is not in database!"));
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String token = jwtUtils.generateTokenFromUsername(user.getUsername());

        logger.info("Token refreshed");

        return new TokenRefreshResponse(token, requestRefreshToken);
    }

    public void logout(Principal principal) {
        if (principal == null) return;

        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        refreshTokenService.deleteByUserId(user.getId());
    }

    public User updateUserProfile(Principal principal, UpdateProfileRequest updateProfileRequest) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal is null");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (updateProfileRequest.getUsername() != null && !updateProfileRequest.getUsername().isEmpty()) {
            if (!updateProfileRequest.getUsername().equals(user.getUsername())) {
                user.setUsername(updateProfileRequest.getUsername());
                user.setProfileName(updateProfileRequest.getUsername());
            }
        }

        if (updateProfileRequest.getEmail() != null && !updateProfileRequest.getEmail().isEmpty()) {
            user.setEmail(updateProfileRequest.getEmail());
        }

        if (updateProfileRequest.getNewPassword() != null && !updateProfileRequest.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateProfileRequest.getNewPassword()));
        }

        if (updateProfileRequest.getProfilePicture() != null && !updateProfileRequest.getProfilePicture().isEmpty()) {
            user.setProfilePicture(updateProfileRequest.getProfilePicture());
        }

        if (updateProfileRequest.getProfileName() != null && !updateProfileRequest.getProfileName().isEmpty()) {
            user.setProfileName(updateProfileRequest.getProfileName());
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            String constraintName = e.getMostSpecificCause().getMessage();
            if (constraintName.contains("users_username_key")) {
                return user;
            }
            throw e;
        }
    }

    public UserProfileResponse getUserProfile(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal is null");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserProfileResponse(user.getUsername(), user.getProfileName(), user.getEmail(), user.getProfilePicture(), user.getAmountOfScripts());
    }
}
