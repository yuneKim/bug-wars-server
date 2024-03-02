package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.LoginDTO;
import net.crusadergames.bugwars.dto.request.SignupDTO;
import net.crusadergames.bugwars.dto.response.JwtDTO;
import net.crusadergames.bugwars.exception.RefreshTokenException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.RefreshToken;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.RoleRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.security.jwt.JwtUtils;
import net.crusadergames.bugwars.security.service.RefreshTokenService;
import net.crusadergames.bugwars.security.service.UserDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AuthServiceTests {
    private RefreshTokenService refreshTokenService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private AuthService authService;
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        refreshTokenService = Mockito.mock(RefreshTokenService.class);
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        emailService = Mockito.mock(EmailService.class);
        JwtUtils jwtUtils = Mockito.mock(JwtUtils.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authService = new AuthService(refreshTokenService, userRepository, roleRepository, passwordEncoder,
                authenticationManager, jwtUtils, emailService);
    }

    @Test
    public void registerUser_returnsUser() {
        SignupDTO signupDTO = new SignupDTO("test_user", "test@gmail.com", "password111");
        User user = new User(signupDTO.getUsername(), signupDTO.getEmail(), signupDTO.getPassword());
        when(userRepository.existsByUsernameIgnoreCase(Mockito.any())).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(Mockito.any())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(roleRepository.findByName(Mockito.any(ERole.class))).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));

        User savedUser = authService.registerUser(signupDTO);

        Assertions.assertThat(savedUser).isNotNull();
    }

    @Test
    public void registerUser_respondsWithConflictStatusOnDuplicateUsername() {
        SignupDTO signupDTO = new SignupDTO("test_user", "test@gmail.com", "password111");
        when(userRepository.existsByUsernameIgnoreCase(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    public void registerUser_respondsWithConflictStatusOnDuplicateEmail() {
        SignupDTO signupDTO = new SignupDTO("test_user", "test@gmail.com", "password111");
        when(userRepository.existsByEmailIgnoreCase(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    public void registerUser_respondsWithErrorMessageOnInappropriateUsername() {
        SignupDTO signupDTO = new SignupDTO("fuck asdfa", "test@gmail.com", "password111");
        when(userRepository.existsByEmailIgnoreCase(Mockito.any())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }

    @Test
    public void registerUser_respondsWithServerErrorOnMissingRole() {
        SignupDTO signupDTO = new SignupDTO("test_user", "test@gmail.com", "password111");
        when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void authenticateUser_returnsJwtResponse() {
        LoginDTO loginDTO = new LoginDTO("test_user", "password111");

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(mockAuthentication);

        UserDetailsImpl mockUserDetails = new UserDetailsImpl(
                1L,
                loginDTO.getUsername(),
                loginDTO.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock token");

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(refreshTokenService.createRefreshToken(Mockito.any())).thenReturn(mockRefreshToken);

        JwtDTO response = authService.authenticateUser(loginDTO.getUsername(), loginDTO.getPassword());

        Assertions.assertThat(response).isNotNull();
    }

    @Test
    public void refreshToken_returnsTokenRefreshResponse() throws RefreshTokenException {
        RefreshToken refreshToken = Mockito.mock(RefreshToken.class);
        User mockUser = new User();
        mockUser.setUsername("test_user");
        when(refreshTokenService.findByToken(Mockito.any())).thenReturn(Optional.of(refreshToken));
        when(refreshToken.getUser()).thenReturn(mockUser);
        Assertions.assertThat(authService.refreshToken("token")).isNotNull();
    }

    @Test
    public void refreshToken_throwsExceptionWhenTokenIsNotFound() {
        when(refreshTokenService.findByToken(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> authService.refreshToken("token"))
                .isInstanceOf(RefreshTokenException.class);
    }

    @Test
    public void logout_deletesRefreshToken() throws ResourceNotFoundException {
        User mockUser = new User();
        mockUser.setId(5L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(mockUser));
        authService.logout("Fred");
        verify(refreshTokenService).deleteByUserId(mockUser.getId());
    }

    @Test
    public void logout_throwsExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("Fred")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> authService.logout("Fred"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
