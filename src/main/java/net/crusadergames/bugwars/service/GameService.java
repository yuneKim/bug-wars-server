package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.GameReplay;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    GameFactory gameFactory;

    public GameReplay playGame(GameRequest gameRequest) {
        List<Swarm> swarms = List.of(
                new Swarm("BLUE", new int[]{33, 5, 0, 35, 0, 12, 35, 0}),
                new Swarm("RED", new int[]{30, 11, 32, 14, 34, 17, 31, 17, 10, 35, 0, 13, 35, 0, 14, 35, 0, 11, 35, 0}),
                new Swarm("YELLOW", new int[]{10}),
                new Swarm("GREEN", new int[]{10})
        );

        Game game = gameFactory.createInstance("ns_fortress4.txt", swarms);
        return game.play();
    }
}
