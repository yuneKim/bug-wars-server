package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.ModifyScriptDTO;
import net.crusadergames.bugwars.exception.AccessDeniedException;
import net.crusadergames.bugwars.exception.ResourceConflictException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.exception.ResourceValidationException;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
    public void getAllNamesOfValidScripts_returnsValidScriptNames() throws Exception {
        List<Script> scripts = List.of(SCRIPT_1, SCRIPT_2);
        when(scriptService.getAllValidScripts()).thenReturn(scripts);

        ResultActions response = mockMvc.perform(get("/api/scripts/all"));


        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()", CoreMatchers.is(2)));
    }

    @Test
    public void getUserScripts_returnsAllScripts() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts")
                .principal(mockPrincipal);

        List<Script> scripts = List.of(SCRIPT_1, SCRIPT_2, SCRIPT_3);
        when(scriptService.getUserScripts(Mockito.any()))
                .thenReturn(scripts);

        ResultActions response = mockMvc.perform(requestBuilder);


        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    public void getUserScripts_respondsWithIntervalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts")
                .principal(mockPrincipal);

        when(scriptService.getUserScripts(Mockito.any()))
                .thenThrow(new ResourceNotFoundException("User not found."));

        ResultActions response = mockMvc.perform(requestBuilder);


        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void getScript_returnsScript() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        when(scriptService.getScript(Mockito.anyLong(), Mockito.any()))
                .thenReturn(SCRIPT_1);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.raw", CoreMatchers.is(SCRIPT_1.getRaw())));

    }

    @Test
    public void getScript_respondsWIthInternalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        when(scriptService.getScript(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new ResourceNotFoundException("User not found."));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void getScript_respondsWIthNotFoundWhenScriptNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        when(scriptService.getScript(Mockito.anyLong(), Mockito.any()))
                .thenThrow(new ResourceNotFoundException("Script not found."));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getScript_respondsWIthForbiddenWhenUserNotAuthorized() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        when(scriptService.getScript(Mockito.anyLong(), Mockito.any()))
                .thenThrow(AccessDeniedException.class);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createScript_returnsScript() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/scripts")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));


        when(scriptService.createScript(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(SCRIPT_1);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SCRIPT_1.getName())));

    }

    @Test
    public void createScript_respondsWithBadRequestWhenScriptNameIsInappropriate() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/scripts")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));


        when(scriptService.createScript(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(ResourceValidationException.class);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createScript_respondsWithInternalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/scripts")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));


        when(scriptService.createScript(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(ResourceNotFoundException.class);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void createScript_respondsWithConflictWhenScriptNameExists() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/scripts")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));


        when(scriptService.createScript(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(ResourceConflictException.class);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void updateScript_returnsScript() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.updateScript(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(SCRIPT_1);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/scripts/{id}", "1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SCRIPT_1.getName())));
    }

    @Test
    public void updateScript_respondsWithInternalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.updateScript(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new ResourceNotFoundException("User not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/scripts/{id}", "1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void updateScript_respondsWithNotFoundWhenScriptNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.updateScript(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new ResourceNotFoundException("Script not found."));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/scripts/{id}", "1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateScript_respondsWithBadRequestWhenScriptNameIsInappropriate() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.updateScript(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(ResourceValidationException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/scripts/{id}", "1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void updateScript_respondsWithForbiddenWhenUserNotAuthorized() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        ModifyScriptDTO modifyScriptDTO = new ModifyScriptDTO("The Ol' Razzle Dazzle", ":START dance");
        when(scriptService.updateScript(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(AccessDeniedException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/scripts/{id}", "1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyScriptDTO));

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void deleteScript_deletesAScript() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteScript_respondsWithInternalServerErrorWhenUserNotFound() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        doThrow(ResourceNotFoundException.class).when(scriptService).deleteScriptById(Mockito.anyLong(), Mockito.any());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void deleteScript_respondsWithForbiddenWhenUserNotAuthorized() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("Fred");

        doThrow(AccessDeniedException.class).when(scriptService).deleteScriptById(Mockito.anyLong(), Mockito.any());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/scripts/{id}", "1")
                .principal(mockPrincipal);

        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}
