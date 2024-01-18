package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.config.Maps;
import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameReplay;
import net.crusadergames.bugwars.dto.response.ResponseGameMap;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.entity.Entity;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.repository.ScriptRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {
    @Mock
    private GameFactory gameFactory;

    @Mock
    private ScriptRepository scriptRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    public void getAllMaps_returnsAllMaps() {
        List<ResponseGameMap> gameMaps = gameService.getAllMaps();

        Assertions.assertThat(gameMaps.size()).isGreaterThan(0);
    }

    @Test
    public void playGame_returnsGameReplay() {
        String mapName = "ns_faceoff.txt";
        GameRequest gameRequest = new GameRequest(List.of(1L, 2L), 1);

        Script script1 = new Script(1L, "Test1", "[13]");
        Script script2 = new Script(2L, "Test2", "[10, 11]");
        List<Swarm> swarms = List.of(
                new Swarm("Test1", new int[]{13}),
                new Swarm("Test2", new int[]{10, 11})
        );
        Game game = Mockito.mock(Game.class);

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script1));
        when(scriptRepository.findById(2L)).thenReturn(Optional.of(script2));
        when(gameFactory.createInstance(mapName, swarms)).thenReturn(game);
        when(game.play()).thenReturn(new GameReplay(null, new Entity[][]{}, null));

        GameReplay replay = gameService.playGame(gameRequest);

        Assertions.assertThat(replay).isNotNull();
    }

    @Test
    public void playGame_throwsHttpStatusExceptionOnInvalidMap() {
        GameRequest gameRequest = new GameRequest(List.of(1L, 2L), -1);

        Assertions.assertThatThrownBy(() -> gameService.playGame(gameRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

        GameRequest gameRequest2 = new GameRequest(List.of(1L, 2L), Maps.getMaps().size() + 1);

        Assertions.assertThatThrownBy(() -> gameService.playGame(gameRequest2))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void playGame_throwsHttpStatusExceptionOnInvalidScript() {
        GameRequest gameRequest = new GameRequest(List.of(1L, 2L), 1);

        Script script1 = new Script(1L, "Test1", "[13]");

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script1));
        when(scriptRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> gameService.playGame(gameRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
