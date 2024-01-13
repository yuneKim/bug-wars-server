package net.crusadergames.bugwars.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Swarm {
    private String name;
    private int[] bytecode;
}
