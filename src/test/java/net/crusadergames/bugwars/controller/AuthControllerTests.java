package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerUser_returnsUser() throws Exception {
        SignupDTO signupDTO = new SignupDTO("test_user", "test@gmail.com", "password111");
        User user = new User(signupDTO.getUsername(), signupDTO.getEmail(), signupDTO.getPassword());
        when(authService.registerUser(ArgumentMatchers.any())).thenReturn(user);

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDTO)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(signupDTO.getUsername())));

    }

    @Test
    public void authenticateUser_returnsJwtResponse() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test_user", "password111");
        JwtDTO jwtDTO = new JwtDTO("accessToken", "refreshToken", "user", List.of("ROLE_USER"));
        when(authService.authenticateUser(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(jwtDTO);

        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.is("accessToken")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", CoreMatchers.is("refreshToken")));
    }

    @Test
    public void refreshToken_returnsTokenRefreshResponse() throws Exception {
        TokenRefreshRequestDTO refreshRequest = new TokenRefreshRequestDTO("refreshToken");
        TokenRefreshResponseDTO refreshResponse = new TokenRefreshResponseDTO("accessToken", "refreshToken");
        when(authService.refreshToken(ArgumentMatchers.any())).thenReturn(refreshResponse);

        ResultActions response = mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.is("accessToken")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", CoreMatchers.is("refreshToken")));
    }

    @Test
    public void refreshToken_respondsWithForbiddenWhenRefreshTokenIsInvalid() throws Exception {
        TokenRefreshRequestDTO refreshRequest = new TokenRefreshRequestDTO("refreshToken");
        when(authService.refreshToken(ArgumentMatchers.any())).thenThrow(RefreshTokenException.class);

        ResultActions response = mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)));

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void logout_returnsOkStatus() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/logout")
                .principal(mockPrincipal);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void logout_handlesNullPrincipal() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void updateProfile_returnsUser() throws Exception {
        UpdateProfileRequestDTO updateProfileRequestDTO = new UpdateProfileRequestDTO("test_user", "testuser", "test@gmail.com", "password111", "password111", "1");
        User user = new User(updateProfileRequestDTO.getUsername(), updateProfileRequestDTO.getEmail(), updateProfileRequestDTO.getNewPassword());

        when(authService.updateUserProfile("test_user", updateProfileRequestDTO)).thenReturn(user);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/update-profile")
                .param("username", "test_user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateProfileRequestDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(updateProfileRequestDTO.getUsername())));
    }


    @Test
    public void getUserProfile_returnsUser() throws Exception {
        UserProfileResponseDTO userProfileResponseDTO = new UserProfileResponseDTO("test_user", "testuser","test@gmail.com", "1", 1);
        User user = new User(userProfileResponseDTO.getUsername(), userProfileResponseDTO.getEmail(), userProfileResponseDTO.getProfilePicture());
        when(authService.getUserProfile(ArgumentMatchers.any())).thenReturn(userProfileResponseDTO);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/user-profile")
                .param("username", "test_user")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(userProfileResponseDTO.getUsername())));

    }

    @Test
    public void logout_respondsWithInternalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");
        doThrow(ResourceNotFoundException.class).when(authService).logout(ArgumentMatchers.anyString());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/auth/logout")
                .principal(mockPrincipal);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
