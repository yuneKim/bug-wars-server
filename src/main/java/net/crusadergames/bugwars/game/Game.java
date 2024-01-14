package net.crusadergames.bugwars.game;

import lombok.Data;
import net.crusadergames.bugwars.dto.response.GameReplay;

import java.util.List;

@Data
public class Game {
    private final Battleground battleground;
    private final List<Swarm> swarms;
    private final GameReplay replay;
    private final int maxMoves;

    public Game(Battleground battleground, List<Swarm> swarms, int maxMoves) {
        this.battleground = battleground;
        this.swarms = swarms;
        this.maxMoves = maxMoves;

        replay = new GameReplay(battleground.getName(), battleground.getGrid(), swarms);
    }

    public GameReplay play() {
        battleground.print();
        for (int i = 0; i < maxMoves; i++) {
            List<ActionSummary> tickActions = battleground.nextTick();
            replay.addTickActions(tickActions);
            battleground.print();
        }
        return replay;
    }


}
