package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.LoginRequest;
import net.crusadergames.bugwars.dto.request.SignupRequest;
import net.crusadergames.bugwars.dto.response.JwtResponse;
import net.crusadergames.bugwars.model.auth.ERole;
import net.crusadergames.bugwars.model.auth.Role;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.RoleRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import net.crusadergames.bugwars.security.jwt.JwtUtils;
import net.crusadergames.bugwars.security.service.UserDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    public void registerUser_returnsUser() {
        SignupRequest signupRequest = new SignupRequest("test_user", "test@gmail.com", "password111");
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        when(userRepository.existsByUsername(Mockito.any())).thenReturn(false);
        when(userRepository.existsByEmail(Mockito.any())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(roleRepository.findByName(Mockito.any(ERole.class))).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));

        User savedUser = authService.registerUser(signupRequest);

        Assertions.assertThat(savedUser).isNotNull();
    }

    @Test
    public void registerUser_respondsWithConflictStatusOnDuplicateUsername() {
        SignupRequest signupRequest = new SignupRequest("test_user", "test@gmail.com", "password111");
        when(userRepository.existsByUsername(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    public void registerUser_respondsWithConflictStatusOnDuplicateEmail() {
        SignupRequest signupRequest = new SignupRequest("test_user", "test@gmail.com", "password111");
        when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    public void registerUser_respondsWithServerErrorOnMissingRole() {
        SignupRequest signupRequest = new SignupRequest("test_user", "test@gmail.com", "password111");
        when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void authenticateUser_returnsJwtResponse() {
        LoginRequest loginRequest = new LoginRequest("test_user", "password111");
        JwtResponse jwtResponse = new JwtResponse("token", "user", List.of("ROLE_USER"));

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(Mockito.any())).thenReturn(mockAuthentication);

        UserDetailsImpl mockUserDetails = new UserDetailsImpl(
                1L,
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        JwtResponse response = authService.authenticateUser(loginRequest);

        Assertions.assertThat(response).isNotNull();
    }
}
