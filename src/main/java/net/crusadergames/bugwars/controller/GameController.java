package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameReplay;
import net.crusadergames.bugwars.dto.response.ResponseGameMap;
import net.crusadergames.bugwars.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    GameService gameService;

    @GetMapping("/maps")
    public List<ResponseGameMap> getAllMaps() {
        return gameService.getAllMaps();
    }

    @PostMapping
    public GameReplay playGame(@RequestBody GameRequest gameRequest) {
        return gameService.playGame(gameRequest);
    }
}
