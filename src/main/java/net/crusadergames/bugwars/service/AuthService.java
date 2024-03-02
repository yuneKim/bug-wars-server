package net.crusadergames.bugwars.service;

import com.modernmt.text.profanity.ProfanityFilter;
import net.crusadergames.bugwars.dto.request.SignupDTO;
import net.crusadergames.bugwars.dto.response.JwtDTO;
import net.crusadergames.bugwars.dto.response.TokenRefreshResponseDTO;
import net.crusadergames.bugwars.exception.RefreshTokenException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.dto.request.UpdateProfileRequestDTO;
import net.crusadergames.bugwars.dto.response.UserProfileResponseDTO;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.RefreshToken;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.RoleRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.security.jwt.JwtUtils;
import net.crusadergames.bugwars.security.service.RefreshTokenService;
import net.crusadergames.bugwars.security.service.UserDetailsImpl;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(RefreshTokenService refreshTokenService, UserRepository userRepository,
                       RoleRepository roleRepository, AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(SignupDTO signUpDTO) {

        if (userRepository.existsByUsernameIgnoreCase(signUpDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        if (userRepository.existsByEmailIgnoreCase(signUpDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }

        ProfanityFilter profanityFilter = new ProfanityFilter();
        if (profanityFilter.test("en", signUpDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inappropriate language.");
        }

        User user = new User(signUpDTO.getUsername(), signUpDTO.getEmail(),
                passwordEncoder.encode(signUpDTO.getPassword()));

        // Set profile name to be equal to username initially
        user.setProfileName(signUpDTO.getUsername());

        Optional<Role> optUserRole = roleRepository.findByName(ERole.ROLE_USER);
        if (optUserRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
        }
        Role userRole = optUserRole.get();
        user.setRoles(Set.of(userRole));
        return userRepository.save(user);
    }

    public JwtDTO authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtDTO(jwt, refreshToken.getToken(), userDetails.getUsername(), roles);
    }

    public TokenRefreshResponseDTO refreshToken(String requestRefreshToken) throws RefreshTokenException {
        RefreshToken refreshToken = refreshTokenService
                .findByToken(requestRefreshToken)
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token is not in database!"));
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String token = jwtUtils.generateTokenFromUsername(user.getUsername());

        return new TokenRefreshResponseDTO(token, requestRefreshToken);
    }

    public void logout(String username) throws ResourceNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        refreshTokenService.deleteByUserId(user.getId());
    }

    public User updateUserProfile(String username, UpdateProfileRequestDTO updateProfileRequestDTO) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (updateProfileRequestDTO.getUsername() != null && !updateProfileRequestDTO.getUsername().isEmpty()) {
            if (!updateProfileRequestDTO.getUsername().equals(user.getUsername())) {
                user.setUsername(updateProfileRequestDTO.getUsername());
                user.setProfileName(updateProfileRequestDTO.getUsername());
            }
        }

        if (updateProfileRequestDTO.getEmail() != null && !updateProfileRequestDTO.getEmail().isEmpty()) {
            user.setEmail(updateProfileRequestDTO.getEmail());
        }

        if (updateProfileRequestDTO.getNewPassword() != null && !updateProfileRequestDTO.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateProfileRequestDTO.getNewPassword()));
        }

        if (updateProfileRequestDTO.getProfilePicture() != null && !updateProfileRequestDTO.getProfilePicture().isEmpty()) {
            user.setProfilePicture(updateProfileRequestDTO.getProfilePicture());
        }

        if (updateProfileRequestDTO.getProfileName() != null && !updateProfileRequestDTO.getProfileName().isEmpty()) {
            user.setProfileName(updateProfileRequestDTO.getProfileName());
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

    public UserProfileResponseDTO getUserProfile(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username from Principal is null");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserProfileResponseDTO(user.getUsername(), user.getProfileName(), user.getEmail(), user.getProfilePicture(), user.getAmountOfScripts());
    }
}