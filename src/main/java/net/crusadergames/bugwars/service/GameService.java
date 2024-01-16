package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameReplay;
import net.crusadergames.bugwars.dto.response.ResponseGameMap;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.repository.GameMapRepository;
import net.crusadergames.bugwars.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    @Autowired
    GameMapRepository gameMapRepository;

    @Autowired
    GameFactory gameFactory;

    @Autowired
    ScriptRepository scriptRepository;


    public List<ResponseGameMap> getAllMaps() {
        return gameMapRepository.findAll()
                .stream()
                .map((map) -> new ResponseGameMap(
                        map.getId(),
                        map.getName(),
                        map.getPreviewImgUrl(),
                        map.getSwarms()
                ))
                .toList();
    }

    // TODO verify scripts are valid
    public GameReplay playGame(GameRequest gameRequest) {
        GameMap map = gameMapRepository.findById(gameRequest.getMapId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Map not found."));
        List<Swarm> swarms = createSwarms(gameRequest.getScriptIds().stream().limit(map.getSwarms()).toList());

        Game game = gameFactory.createInstance(map.getFileName(), swarms);
        return game.play();
    }

    private List<Swarm> createSwarms(List<Long> scriptIds) {
        List<Swarm> swarms = new ArrayList<>();
        for (Long id : scriptIds) {
            Script script = scriptRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid script."));
            swarms.add(new Swarm(
                    script.getName(),
                    script.deserializeBytecode()
            ));
        }
        return swarms;
    }
}
