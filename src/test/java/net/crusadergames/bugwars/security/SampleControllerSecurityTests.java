package net.crusadergames.bugwars.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.model.SampleString;
import net.crusadergames.bugwars.service.SampleStringService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class SampleControllerSecurityTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @LocalServerPort
    private Integer port;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SampleStringService sampleStringService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void getAll_returnalsAllUsersWhenNotAuthenticated() throws Exception {
        SampleString string = SampleString.builder().content("chocolate").build();
        when(sampleStringService.getAll()).thenReturn(List.of(string));

        ResultActions response = mockMvc.perform(get("/api/sampleStrings"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(1)));
    }

    @Test
    @WithMockUser
    public void getAll_returnalsAllUsersWhenAuthenticated() throws Exception {
        SampleString string = SampleString.builder().content("chocolate").build();
        when(sampleStringService.getAll()).thenReturn(List.of(string));

        ResultActions response = mockMvc.perform(get("/api/sampleStrings"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(1)));
    }

    @Test
    public void addUser_respondsWithUnauthorizedWhenNotAuthenticated() throws Exception {
        SampleString string = SampleString.builder().content("polar bear").build();
        when(sampleStringService.addString(ArgumentMatchers.any())).thenReturn(string);

        ResultActions response = mockMvc.perform(post("/api/sampleStrings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(string)));

        response.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void addUser_returnsCreatedStringWhenAuthenticated() throws Exception {
        SampleString string = SampleString.builder().content("polar bear").build();
        when(sampleStringService.addString(ArgumentMatchers.any())).thenReturn(string);

        ResultActions response = mockMvc.perform(post("/api/sampleStrings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(string)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(string.getContent())));
    }
}
