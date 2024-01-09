package net.crusadergames.bugwars.game;

import lombok.Data;

import java.util.List;

@Data
public class Game {
    private static int MAX_MOVES = 3000;

    private final Battleground battleground;
    private final List<Swarm> swarms;
    private final GameReplay replay;

    public Game(Battleground battleground, List<Swarm> swarms) {
        this.battleground = battleground;
        this.swarms = swarms;

        replay = new GameReplay(battleground.getName(), battleground.getGrid());
    }

    public GameReplay play() {
        for (int i = 0; i < MAX_MOVES; i++) {
            List<ActionSummary> tickActions = battleground.nextTick();
            replay.addTickActions(tickActions);
        }
        return replay;
    }


}
