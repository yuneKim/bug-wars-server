package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameReplay;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.Script;
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
    GameFactory gameFactory;

    @Autowired
    ScriptRepository scriptRepository;

    // TODO verify scripts are valid
    // TODO mapName is currently file name, fix it
    public GameReplay playGame(GameRequest gameRequest) {
        List<Swarm> swarms = createSwarms(gameRequest.getScriptIds());

        Game game = gameFactory.createInstance(gameRequest.getMapName(), swarms);
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
