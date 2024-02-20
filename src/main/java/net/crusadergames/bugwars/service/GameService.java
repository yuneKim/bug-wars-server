package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.config.Maps;
import net.crusadergames.bugwars.dto.request.PlayGameDTO;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;
import net.crusadergames.bugwars.dto.response.GameMapDTO;
import net.crusadergames.bugwars.game.Game;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.setup.GameFactory;
import net.crusadergames.bugwars.model.GameMap;
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


    public List<GameMapDTO> getAllMaps() {
        List<GameMapDTO> maps = new ArrayList<>();

        for (int i = 0; i < Maps.getMaps().size(); i++) {
            GameMap map = Maps.getMaps().get(i);
            maps.add(
                    new GameMapDTO(
                            i,
                            map.getName(),
                            map.getPreviewImgUrl(),
                            map.getSwarms()
                    )
            );
        }

        return maps;
    }

    public GameReplayDTO playGame(PlayGameDTO playGameDTO) {
        if (playGameDTO.getMapId() > Maps.getMaps().size() || playGameDTO.getMapId() < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Map not found.");
        }
        GameMap map = Maps.getMaps().get(playGameDTO.getMapId());
        List<Swarm> swarms = createSwarms(playGameDTO.getScriptIds().stream().limit(map.getSwarms()).toList());

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
                    script.getUser().getUsername(),
                    script.deserializeBytecode()
            ));
        }
        return swarms;
    }
}
