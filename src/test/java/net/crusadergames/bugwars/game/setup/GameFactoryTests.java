package net.crusadergames.bugwars.game.setup;

import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameFactoryTests {
    private static ResourceLoader testLoader;

    @Mock
    ResourceLoader loader;

    @InjectMocks
    GameFactory gameFactory;

    @BeforeAll
    public static void setup() {
        testLoader = new DefaultResourceLoader();

    }

    @Test
    public void createsGameInstance() {
        when(loader.getResource(Mockito.anyString())).thenAnswer((invocation) -> testLoader.getResource(invocation.getArgument(0)));
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10}),
                new Swarm("Swarm 4", new int[]{10})
        );

        Game game = gameFactory.createInstance("ns_arena_mini.txt", swarms);
        Assertions.assertThat(game.getBattleground().getBugs().size()).isEqualTo(12);
    }

    @Test
    public void throwsGameInitializationExceptionOnInvalidMap() {
        when(loader.getResource(Mockito.anyString())).thenAnswer((invocation) -> testLoader.getResource(invocation.getArgument(0)));
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10}),
                new Swarm("Swarm 4", new int[]{10})
        );

        Assertions.assertThatThrownBy(() -> gameFactory.createInstance("ns_arena_mini_invalid.txt", swarms))
                .isInstanceOf(GameInitializationException.class);
    }

    @Test
    public void throwsGameInitializationExceptionOnFailureToLoadMap() {
        when(loader.getResource(Mockito.anyString())).thenAnswer((invocation) -> testLoader.getResource(invocation.getArgument(0)));
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10}),
                new Swarm("Swarm 4", new int[]{10})
        );

        Assertions.assertThatThrownBy(() -> gameFactory.createInstance("ns_arena_mi.txt", swarms))
                .isInstanceOf(GameInitializationException.class);
    }
}
