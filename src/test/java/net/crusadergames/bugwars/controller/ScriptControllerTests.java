package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.CreateScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.service.ScriptService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

@WebMvcTest(controllers = ScriptController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ScriptControllerTests {

    private final User USER = new User("tortellini", "gmail@email.com", "passingTests");
    private final Script SCRIPT_1 = new Script(1L, USER, "The Ol' Razzle Dazzle", ":START dance", "20 49 103 952 1039 59 30 10", true);
    private final Script SCRIPT_2 = new Script(2L, USER, "Sneaky Peeky", ":START :END", "03 050 20 50 03 06 10 50", true);
    private final Script SCRIPT_3 = new Script(3L, USER, "Burger Bite", ":START att ifEnemy bite", "05 30 0t 30 f05 52c go2", true);


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScriptService scriptService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void list_returnsAllScripts() throws Exception {
        List<Script> scripts = List.of(SCRIPT_1, SCRIPT_2, SCRIPT_3);
        when(scriptService.getUserScripts(Mockito.any()))
                .thenReturn(scripts);

        ResultActions response = mockMvc.perform(get("/api/scripts"));


        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    public void getScript_returnsScript() throws Exception {
        when(scriptService.getScript(Mockito.anyLong(), Mockito.any()))
                .thenReturn(SCRIPT_1);

        ResultActions response = mockMvc.perform(get("/api/scripts/{id}", "1"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.raw", CoreMatchers.is(SCRIPT_1.getRaw())));

    }

    @Test
    public void createScript_returnsScript() throws Exception {
        CreateScriptRequest createScriptRequest = new CreateScriptRequest("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.createScript(Mockito.any(), Mockito.any()))
                .thenReturn(SCRIPT_1);

        ResultActions response = mockMvc.perform(post("/api/scripts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createScriptRequest)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SCRIPT_1.getName())));

    }

}
