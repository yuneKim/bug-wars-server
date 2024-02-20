package net.crusadergames.bugwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.crusadergames.bugwars.dto.request.PlayGameDTO;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.service.GameService;
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

@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class GameControllerTests {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @MockBean
    private GameFactory gameFactory;

    @Test
    public void getAllMaps_returnsAllMaps() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/game/maps"));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void playGame_returnsGameResponse() throws Exception {
        PlayGameDTO createPlayGameDTO = new PlayGameDTO(List.of(1L, 2L), 1);
        GameReplayDTO gameReplayDTO = Mockito.mock(GameReplayDTO.class);
        when(gameService.playGame(Mockito.any(), Mockito.any())).thenReturn(gameReplayDTO);


        ResultActions response = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPlayGameDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void playGame_respondsWithNotFoundWhenMapNotFound() throws Exception {
        PlayGameDTO createPlayGameDTO = new PlayGameDTO(List.of(1L, 2L), -1);
        when(gameService.playGame(Mockito.any(), Mockito.any())).thenThrow(new ResourceNotFoundException("Map not found."));

        ResultActions response = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPlayGameDTO)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void playGame_respondsWithUnprocessableEntityWhenScriptInvalid() throws Exception {
        PlayGameDTO createPlayGameDTO = new PlayGameDTO(List.of(1L, 2L), 1);
        when(gameService.playGame(Mockito.any(), Mockito.any())).thenThrow(new ResourceNotFoundException("Invalid Script."));

        ResultActions response = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPlayGameDTO)));

        response.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
