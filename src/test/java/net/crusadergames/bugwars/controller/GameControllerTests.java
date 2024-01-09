package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameResponse;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class GameControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;

    @MockBean
    private GameFactory gameFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void playGame_returnsGameResponse() throws Exception {
        GameRequest createGameRequest = new GameRequest(List.of(Mockito.mock(Swarm.class)), "Columbus");
        GameResponse gameResponse = new GameResponse();
        when(gameService.playGame(Mockito.any())).thenReturn(gameResponse);


        ResultActions response = mockMvc.perform(post("/api/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGameRequest)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
