package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.model.SampleString;
import net.crusadergames.bugwars.service.SampleStringService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = SampleStringController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class SampleControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SampleStringService sampleStringService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAll_returnsAllUsers() throws Exception {
        SampleString string = SampleString.builder().content("chocolate").build();
        when(sampleStringService.getAll()).thenReturn(List.of(string));

        ResultActions response = mockMvc.perform(get("/api/sampleStrings"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(1)));
    }

    @Test
    public void addUser_returnsCreated() throws Exception {
        SampleString string = SampleString.builder().content("polar bear").build();
        when(sampleStringService.addString(ArgumentMatchers.any())).thenReturn(string);

        ResultActions response = mockMvc.perform(post("/api/sampleStrings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(string)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(string.getContent())));
    }
}
