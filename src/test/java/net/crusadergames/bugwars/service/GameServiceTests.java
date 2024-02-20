package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.config.Maps;
import net.crusadergames.bugwars.dto.request.PlayGameDTO;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.entity.Entity;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.ScriptRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class GameServiceTests {
    private GameFactory gameFactory;
    private ScriptRepository scriptRepository;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        gameFactory = Mockito.mock(GameFactory.class);
        scriptRepository = Mockito.mock(ScriptRepository.class);
        gameService = new GameService(gameFactory, scriptRepository);
    }

    @Test
    public void getAllMaps_returnsAllMaps() {
        List<GameMap> gameMaps = gameService.getAllMaps();

        Assertions.assertThat(gameMaps.size()).isGreaterThan(0);
    }

    @Test
    public void playGame_returnsGameReplay() throws ResourceNotFoundException {
        String mapName = "ns_faceoff.txt";
        PlayGameDTO playGameDTO = new PlayGameDTO(List.of(1L, 2L), 1);

        Script script1 = new Script(1L, "Test1", "[13]");
        script1.setUser(new User("User1", "user@user.com", "password"));
        Script script2 = new Script(2L, "Test2", "[10, 11]");
        script2.setUser(new User("User2", "user@user.com", "password"));
        List<Swarm> swarms = List.of(
                new Swarm("Test1", "User1", new int[]{13}),
                new Swarm("Test2", "User2", new int[]{10, 11})
        );
        Game game = Mockito.mock(Game.class);

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script1));
        when(scriptRepository.findById(2L)).thenReturn(Optional.of(script2));
        when(gameFactory.createInstance(mapName, swarms)).thenReturn(game);
        when(game.play()).thenReturn(new GameReplayDTO(null, new Entity[][]{}, null));

        GameReplayDTO replay = gameService.playGame(playGameDTO.getScriptIds(), playGameDTO.getMapId());

        Assertions.assertThat(replay).isNotNull();
    }

    @Test
    public void playGame_throwsHttpStatusExceptionOnInvalidMap() {
        PlayGameDTO playGameDTO = new PlayGameDTO(List.of(1L, 2L), -1);

        Assertions.assertThatThrownBy(() -> gameService.playGame(playGameDTO.getScriptIds(), playGameDTO.getMapId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Map not found.");

        PlayGameDTO playGameDTO2 = new PlayGameDTO(List.of(1L, 2L), Maps.getMaps().size() + 1);

        Assertions.assertThatThrownBy(() -> gameService.playGame(playGameDTO2.getScriptIds(), playGameDTO2.getMapId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Map not found.");
    }

    @Test
    public void playGame_throwsHttpStatusExceptionOnInvalidScript() {
        PlayGameDTO playGameDTO = new PlayGameDTO(List.of(1L, 2L), 1);

        Script script1 = new Script(1L, "Test1", "[13]");
        script1.setUser(new User("User1", "user@user.com", "password"));

        when(scriptRepository.findById(1L)).thenReturn(Optional.of(script1));
        when(scriptRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> gameService.playGame(playGameDTO.getScriptIds(), playGameDTO.getMapId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Invalid Script.");
    }
}
