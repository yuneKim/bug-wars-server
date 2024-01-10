package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.GameReplay;
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

    public GameReplay playGame(GameRequest gameRequest) {
        List<Swarm> swarms = createSwarms(gameRequest.getScriptIds());

        List<Swarm> swarms = List.of(
                new Swarm("BLUE", new int[]{33, 5, 0, 35, 0, 12, 35, 0}),
                new Swarm("RED", new int[]{30, 11, 32, 14, 34, 17, 31, 17, 10, 35, 0, 13, 35, 0, 14, 35, 0, 11, 35, 0}),
                new Swarm("YELLOW", new int[]{10}),
                new Swarm("GREEN", new int[]{10})
        );

        Game game = gameFactory.createInstance("ns_fortress4.txt", swarms);
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
