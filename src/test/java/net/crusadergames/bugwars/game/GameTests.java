package net.crusadergames.bugwars.game;

import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.game.entity.Entity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class GameTests {

    @Test
    public void simulatesCorrectNumberOfTicks() {
        Battleground battleground = Mockito.mock(Battleground.class);
        List<Swarm> swarms = List.of(new Swarm("Swarm1", "User1", new int[]{10}), new Swarm("Swarm2", "User2", new int[]{10}));

        when(battleground.getGrid()).thenReturn(new Entity[][]{});
        when(battleground.nextTick()).thenReturn(new TickSummary());
        Game game = new Game(battleground, swarms, 25);
        game.play();

        Mockito.verify(battleground, times(25)).nextTick();
    }

    @Test
    public void returnsGameReplay() {
        Battleground battleground = Mockito.mock(Battleground.class);
        List<Swarm> swarms = List.of(new Swarm("Swarm1", "User1", new int[]{10}), new Swarm("Swarm2", "User2", new int[]{10}));

        when(battleground.getGrid()).thenReturn(new Entity[][]{});
        when(battleground.nextTick()).thenReturn(new TickSummary());
        Game game = new Game(battleground, swarms, 100);
        GameReplayDTO gameReplayDTO = game.play();
        Assertions.assertThat(gameReplayDTO).isNotNull();
        Mockito.verify(battleground, times(100)).nextTick();
    }

    @Test
    public void endsEarlyIfOneSwarmLeft() {
        Battleground battleground = Mockito.mock(Battleground.class);
        List<Swarm> swarms = List.of(new Swarm("Swarm1", "User1", new int[]{10}), new Swarm("Swarm2", "User2", new int[]{10}));

        when(battleground.getGrid()).thenReturn(new Entity[][]{});
        when(battleground.nextTick()).thenReturn(new TickSummary(List.of(), true));
        Game game = new Game(battleground, swarms, 100);
        GameReplayDTO gameReplayDTO = game.play();
        Assertions.assertThat(gameReplayDTO).isNotNull();
        Mockito.verify(battleground, times(1)).nextTick();
    }
}
