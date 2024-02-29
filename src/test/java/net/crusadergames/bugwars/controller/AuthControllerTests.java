package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.LoginRequest;
import net.crusadergames.bugwars.dto.request.SignupRequest;
import net.crusadergames.bugwars.dto.request.TokenRefreshRequest;
import net.crusadergames.bugwars.dto.request.UpdateProfileRequest;
import net.crusadergames.bugwars.dto.response.JwtResponse;
import net.crusadergames.bugwars.dto.response.TokenRefreshResponse;
import net.crusadergames.bugwars.dto.response.UserProfileResponse;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.service.AuthService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
        SignupRequest signupRequest = new SignupRequest("test_user", "test@gmail.com", "password111");
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), signupRequest.getPassword());
        when(authService.registerUser(ArgumentMatchers.any())).thenReturn(user);

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(signupRequest.getUsername())));

    }

    @Test
    public void authenticateUser_returnsJwtResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test_user", "password111");
        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken", "user", List.of("ROLE_USER"));
        when(authService.authenticateUser(ArgumentMatchers.any())).thenReturn(jwtResponse);

        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.is("accessToken")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", CoreMatchers.is("refreshToken")));
    }

    @Test
    public void refreshToken_returnsTokenRefreshResponse() throws Exception {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest("refreshToken");
        TokenRefreshResponse refreshResponse = new TokenRefreshResponse("accessToken", "refreshToken");
        when(authService.refreshToken(ArgumentMatchers.any())).thenReturn(refreshResponse);

        ResultActions response = mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", CoreMatchers.is("accessToken")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", CoreMatchers.is("refreshToken")));
    }

    @Test
    public void logout_returnsOkStatus() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void updateProfile_returnsUser() throws Exception {
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("test_user", "testuser", "test@gmail.com", "password111", "password111", "1");
        User user = new User(updateProfileRequest.getUsername(), updateProfileRequest.getEmail(), updateProfileRequest.getNewPassword());

        when(authService.updateUserProfile(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(user);

        ResultActions response = mockMvc.perform(put("/api/auth/update-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(updateProfileRequest.getUsername())));

    }

    @Test
    public void getUserProfile_returnsUser() throws Exception {
        UserProfileResponse userProfileResponse = new UserProfileResponse("test_user", "testuser","test@gmail.com", "1", 1);
        User user = new User(userProfileResponse.getUsername(), userProfileResponse.getEmail(), userProfileResponse.getProfilePicture());
        when(authService.getUserProfile(ArgumentMatchers.any())).thenReturn(userProfileResponse);

        ResultActions response = mockMvc.perform(get("/api/auth/user-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileResponse)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", CoreMatchers.is(userProfileResponse.getUsername())));

    }
}
