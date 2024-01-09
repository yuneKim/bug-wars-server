package net.crusadergames.bugwars.game.setup;

import net.crusadergames.bugwars.game.Battleground;
import net.crusadergames.bugwars.game.GameInitializationException;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.entity.Bug;
import net.crusadergames.bugwars.game.entity.Food;
import net.crusadergames.bugwars.game.entity.Wall;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BattlegroundFactoryTests {
    private static final String BASE_PATH = "classpath:maps/";
    private static ResourceLoader loader;

    @BeforeAll
    public static void setup() {
        loader = new DefaultResourceLoader();

    }

    @Test
    public void returnsCreatedBattleground() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10}),
                new Swarm("Swarm 4", new int[]{10})
        );

        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        System.out.println(battleground);
        Assertions.assertThat(battleground.getBugs().size()).isEqualTo(12);
        Assertions.assertThat(battleground.getName()).isEqualTo("Arena Mini");
        Assertions.assertThat(List.of(battleground.getGrid()[0])).extracting("class")
                .containsOnly(Wall.class);
        Assertions.assertThat(Stream.of(battleground.getGrid()[1]).filter(Objects::nonNull).toList()).extracting("class")
                .contains(Wall.class, Bug.class).hasSize(6);
        Assertions.assertThat(Stream.of(battleground.getGrid()[3]).filter(Objects::nonNull).toList()).extracting("class")
                .contains(Wall.class, Food.class);
    }

    @Test
    public void returnsCreatedBattlegroundWithFewerThanMaximumSwarms() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mini.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10})
        );

        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();
        System.out.println(battleground);
        Assertions.assertThat(battleground.getBugs().size()).isEqualTo(9);
        Assertions.assertThat(Stream.of(battleground.getGrid()[1]).filter(Objects::nonNull).toList()).extracting("class")
                .contains(Wall.class, Bug.class).hasSize(6);
        Assertions.assertThat(Stream.of(battleground.getGrid()[5]).filter((e) -> e instanceof Bug).toList().size()).isEqualTo(2);
    }

    @Test
    public void throwsGameInitializationExceptionOnResourceIOException() {
        Resource mapFile = loader.getResource(BASE_PATH + "ns_arena_mi.txt");
        List<Swarm> swarms = List.of(
                new Swarm("Swarm 1", new int[]{10}),
                new Swarm("Swarm 2", new int[]{10}),
                new Swarm("Swarm 3", new int[]{10}),
                new Swarm("Swarm 4", new int[]{10})
        );
        Assertions.assertThatThrownBy(() -> new BattlegroundFactory(mapFile, swarms).createBattleground())
                .isInstanceOf(GameInitializationException.class);
    }
}
