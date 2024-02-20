package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.PlayGameDTO;
import net.crusadergames.bugwars.dto.response.GameMapDTO;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/game")
public class GameController {
    GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/maps")
    public List<GameMapDTO> getAllMaps() {
        List<GameMap> maps = gameService.getAllMaps();
        List<GameMapDTO> dtoMaps = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
            GameMap map = maps.get(i);
            dtoMaps.add(
                    new GameMapDTO(
                            i,
                            map.getName(),
                            map.getPreviewImgUrl(),
                            map.getSwarms()
                    )
            );
        }

        return dtoMaps;
    }

    @PostMapping
    public GameReplayDTO playGame(@RequestBody PlayGameDTO playGameDTO) {
        try {
            return gameService.playGame(playGameDTO.getScriptIds(), playGameDTO.getMapId());
        } catch (ResourceNotFoundException e) {
            if (e.getMessage().equals("Map not found.")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Script.");
            }
        }
    }
}
