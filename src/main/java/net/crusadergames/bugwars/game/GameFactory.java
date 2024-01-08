package net.crusadergames.bugwars.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameFactory {
    static private final String BASE_PATH = "classpath:maps/";

    @Autowired
    ResourceLoader loader;

    public Game createInstance(String fileName, List<Swarm> swarms) {
        Resource mapFile = loader.getResource(BASE_PATH + fileName);
        Battleground battleground = new BattlegroundFactory(mapFile, swarms).createBattleground();

        return new Game(battleground, swarms);
    }

}
