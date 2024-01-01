package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.BugAssemblyParseRequest;
import net.crusadergames.bugwars.service.BugAssemblyParserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = BugAssemblyParserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class BugAssemblyParserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BugAssemblyParserService bugAssemblyParserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void parse_returnsBytecode() throws Exception {
        BugAssemblyParseRequest bugAssemblyParseRequest = new BugAssemblyParseRequest(":START\ngoto START");
        when(bugAssemblyParserService.parse(ArgumentMatchers.any())).thenReturn(List.of(35, 0));

        ResultActions response = mockMvc.perform(post("/api/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bugAssemblyParseRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]", CoreMatchers.is(35)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]", CoreMatchers.is(0)));

    }
}
