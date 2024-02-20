package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.PlayGameDTO;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.dto.response.GameMapDTO;
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
    public List<GameMapDTO> getAllMaps() {
        return gameService.getAllMaps();
    }

    @PostMapping
    public GameReplayDTO playGame(@RequestBody PlayGameDTO playGameDTO) {
        return gameService.playGame(playGameDTO);
    }
}
