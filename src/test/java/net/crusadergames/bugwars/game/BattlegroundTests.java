package net.crusadergames.bugwars.game;

import net.crusadergames.bugwars.game.entity.Bug;
import net.crusadergames.bugwars.game.entity.Entity;
import net.crusadergames.bugwars.game.entity.Food;
import net.crusadergames.bugwars.game.setup.BattlegroundFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

public class BattlegroundTests {
    private static final String BASE_PATH = "classpath:maps/";
    private static ResourceLoader loader;

    @BeforeAll
    public static void setup() {
        loader = new DefaultResourceLoader();
    }

    @Test
    public void returnsTickSummary() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{11}),
                new Swarm("Swarm 3", new int[]{12}),
                new Swarm("Swarm 4", new int[]{13})
        );
        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        TickSummary tickSummary = battleground.nextTick();
        Assertions.assertThat(tickSummary).isNotNull();
        Assertions.assertThat(tickSummary.getSummary().size()).isGreaterThan(0);
    }

    @Test
    public void throwsRuntimeExceptionOnInvalidAction() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{9}),
                new Swarm("Swarm 2", new int[]{11}),
                new Swarm("Swarm 3", new int[]{12}),
                new Swarm("Swarm 4", new int[]{13})
        );
        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        Assertions.assertThatThrownBy(battleground::nextTick)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void handlesAttAction() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini3.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{13})
        );
        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        for (int i = 0; i < 3; i++) {
            battleground.nextTick();
        }
        Entity expectFood = battleground.getGrid()[4][2];
        Assertions.assertThat(expectFood).isInstanceOf(Food.class);
        battleground.nextTick();
        Entity expectNull = battleground.getGrid()[4][2];
        Assertions.assertThat(expectNull).isNull();
    }

    @Test
    public void handlesEatAction() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini3.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{13, 14})
        );
        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        for (int i = 0; i < 3; i++) {
            battleground.nextTick();
        }
        Entity expectFood = battleground.getGrid()[4][2];
        Assertions.assertThat(expectFood).isInstanceOf(Food.class);
        battleground.nextTick();
        Entity expectBug = battleground.getGrid()[4][2];
        Assertions.assertThat(expectBug).isInstanceOf(Bug.class);
    }
}
