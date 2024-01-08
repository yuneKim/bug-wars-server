package net.crusadergames.bugwars.game;

import lombok.Data;

import java.util.List;

@Data
public class Game {
    private static int MAX_MOVES = 30;

    private final Battleground battleground;
    private final List<Swarm> swarms;

    public Game(Battleground battleground, List<Swarm> swarms) {
        this.battleground = battleground;
        this.swarms = swarms;

    }

    public void play() {
        battleground.print();

        for (int i = 0; i < MAX_MOVES; i++) {
            battleground.nextTick();
            battleground.print();
        }

    }


}
