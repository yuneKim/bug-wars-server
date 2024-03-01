package net.crusadergames.bugwars.game;

import lombok.Data;
import net.crusadergames.bugwars.dto.response.GameReplayDTO;

import java.util.List;

@Data
public class Game {
    private final Battleground battleground;
    private final List<Swarm> swarms;
    private final GameReplayDTO replay;
    private final int maxMoves;

    public Game(Battleground battleground, List<Swarm> swarms, int maxMoves) {
        this.battleground = battleground;
        this.swarms = swarms;
        this.maxMoves = maxMoves;

        replay = new GameReplayDTO(battleground.getName(), battleground.getGrid(), swarms);
    }

    public GameReplayDTO play() {
        for (int i = 0; i < maxMoves; i++) {
            TickSummary tickSummary = battleground.nextTick();
            replay.addTickActions(tickSummary.summary);

            if (tickSummary.lastSwarmStanding) break;
        }
        return replay;
    }
}
