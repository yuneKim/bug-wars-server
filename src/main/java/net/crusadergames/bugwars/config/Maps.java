package net.crusadergames.bugwars.config;

import net.crusadergames.bugwars.model.GameMap;

import java.util.ArrayList;
import java.util.List;

public class Maps {
    public static List<GameMap> getMaps() {
        List<GameMap> maps = new ArrayList<>();

        maps.add(new GameMap("Arena", "ns_arena.txt", "https://i.imgur.com/e3tJLId.png", 2));
        maps.add(new GameMap("Faceoff", "ns_faceoff.txt", "https://i.imgur.com/9cx1CUP.png", 2));
        maps.add(new GameMap("Maelstrom", "ns_maelstrom.txt", "https://i.imgur.com/8bjV0fS.png", 2));
        maps.add(new GameMap("Grand Arena", "ns_grandarena4.txt", "https://i.imgur.com/eEfG02z.png", 4));
        maps.add(new GameMap("Fortress", "ns_fortress4.txt", "https://i.imgur.com/NXFJ90l.png", 4));
        maps.add(new GameMap("Pickaxe", "ns_pickaxe4.txt", "https://i.imgur.com/opEjNlc.png", 4));

        return maps;
    }
}
