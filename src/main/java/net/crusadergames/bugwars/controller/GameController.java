package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameResponse;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.GameFactory;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    GameFactory gameFactory;

    @Autowired
    GameService gameService;

    @GetMapping
    public void play() {
        List<Swarm> swarms = List.of(
                new Swarm("BLUE", new int[]{33, 5, 0, 35, 0, 12, 35, 0}),
                new Swarm("RED", new int[]{30, 11, 32, 14, 34, 17, 31, 17, 10, 35, 0, 13, 35, 0, 14, 35, 0, 11, 35, 0})
//                new Swarm("YELLOW", new int[]{10}),
//                new Swarm("GREEN", new int[]{10})
        );

        Game game = gameFactory.createInstance("ns_arena.txt", swarms);
        game.play();
    }

    @PostMapping
    public GameResponse playGame(@RequestBody GameRequest gameRequest) {
        return gameService.playGame(gameRequest);
    }
}
