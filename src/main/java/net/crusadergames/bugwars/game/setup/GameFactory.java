package net.crusadergames.bugwars.game.setup;

import net.crusadergames.bugwars.game.Battleground;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GameFactory {
    static private final String BASE_PATH = "classpath:maps/";

    @Autowired
    ResourceLoader loader;

    public Game createInstance(String fileName, List<Swarm> swarms) {
        Resource mapFile = loader.getResource(BASE_PATH + fileName);

        try {
            MapValidator mapValidator = new MapValidator(mapFile.getContentAsString(StandardCharsets.UTF_8).split("\\R"));
            boolean mapGood = mapValidator.validate();
            System.out.println(mapGood);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();

        return new Game(battleground, swarms);
    }

}
