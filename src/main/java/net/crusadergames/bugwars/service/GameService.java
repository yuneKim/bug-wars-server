package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.config.Maps;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.GameMap;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.repository.ScriptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    GameFactory gameFactory;
    ScriptRepository scriptRepository;

    public GameService(GameFactory gameFactory, ScriptRepository scriptRepository) {
        this.gameFactory = gameFactory;
        this.scriptRepository = scriptRepository;
    }

    public List<GameMap> getAllMaps() {
        return Maps.getMaps();
    }

    public GameReplayDTO playGame(List<Long> scriptIds, Integer mapId) throws ResourceNotFoundException {
        if (mapId > Maps.getMaps().size() || mapId < 0) {
            throw new ResourceNotFoundException("Map not found.");
        }

        GameMap map = Maps.getMaps().get(mapId);
        List<Swarm> swarms = createSwarms(scriptIds.stream().limit(map.getSwarms()).toList());

        Game game = gameFactory.createInstance(map.getFileName(), swarms);
        return game.play();
    }

    private List<Swarm> createSwarms(List<Long> scriptIds) throws ResourceNotFoundException {
        List<Swarm> swarms = new ArrayList<>();
        for (Long id : scriptIds) {
            Script script = scriptRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid Script."));
            swarms.add(new Swarm(
                    script.getName(),
                    script.getUser().getUsername(),
                    script.deserializeBytecode()
            ));
        }
        return swarms;
    }
}
