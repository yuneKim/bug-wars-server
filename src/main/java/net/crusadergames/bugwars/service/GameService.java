package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.GameRequest;
import net.crusadergames.bugwars.dto.response.GameResponse;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.GameFactory;
import net.crusadergames.bugwars.game.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class GameService {

    @Autowired
    GameFactory gameFactory;

    public GameResponse playGame(GameRequest gameRequest) {
        Entity[][] entity = new Entity[0][];
        List<List<?>> list = null;

        Game game = gameFactory.createInstance(gameRequest.getMapName(), gameRequest.getSwarm());

        return new GameResponse(entity, list);
    }
}
