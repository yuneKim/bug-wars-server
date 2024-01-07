package net.crusadergames.bugwars.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class Game {
    @Autowired
    private ResourceLoader loader;
    private GameMap map;

    public void play() {
        map = GameMap
                .load("classpath:maps/ns_fortress4.txt", loader)
                .createSwarm("BLUE", new int[]{10})
                .createSwarm("RED", new int[]{11})
                .createSwarm("YELLOW", new int[]{11})
                .createSwarm("GREEN", new int[]{11});
        map.print();
    }
}
