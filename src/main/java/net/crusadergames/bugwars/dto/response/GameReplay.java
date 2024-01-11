package net.crusadergames.bugwars.dto.response;

import lombok.Data;
import net.crusadergames.bugwars.game.ActionSummary;
import net.crusadergames.bugwars.game.entity.Bug;
import net.crusadergames.bugwars.game.entity.Direction;
import net.crusadergames.bugwars.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class GameReplay {
    private static Map<String, Integer> ENTITY_MAP = Map.of(
            "Null", 0,
            "Wall", 1,
            "Food", 2,
            "Bug", 3
    );

    private static Map<Direction, Integer> DIRECTION_MAP = Map.of(
            Direction.NORTH, 0,
            Direction.EAST, 1,
            Direction.SOUTH, 2,
            Direction.WEST, 3
    );

    private final String map;
    private final String battleground;
    private final List<List<ActionSummary>> replay;

    public GameReplay(String map, Entity[][] grid) {
        this.map = map;
        this.battleground = squashBattleground(grid);
        this.replay = new ArrayList<>();
    }

    public void addTickActions(List<ActionSummary> tickActions) {
        replay.add(tickActions);
    }

    /*
     * Squashing each grid entity into 6 bits (max int value of 63, padded with a leading zero if less than 2 digits)
     *
     * Swarm                Direction           Entity
     * (Only Bugs)          (Only Bugs)         (All)
     *
     * Swarm 1  00          North  00            None  00
     * Swarm 2  01          East   01            Wall  01
     * Swarm 3  10          South  10            Food  10
     * Swarm 4  11          West   11            Bug   11
     */
    private String squashBattleground(Entity[][] grid) {
        StringBuilder battlegroundString = new StringBuilder();
        for (Entity[] entities : grid) {
            for (Entity e : entities) {
                String className = e == null ? "Null" : e.getClass().getSimpleName();
                int n = ENTITY_MAP.get(className);
                if (className.equals("Bug")) {
                    Bug bug = (Bug) e;
                    n += DIRECTION_MAP.get(bug.getDirection()) << 2;
                    n += (bug.getSwarm()) << 4;
                }
                battlegroundString.append(String.format("%02d", n));
            }
            battlegroundString.append(" ");
        }
        return battlegroundString.toString();
    }
}
