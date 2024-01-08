package net.crusadergames.bugwars.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Food implements Entity, Attackable {
    @Override
    public String toString() {
        return "a";
    }
}
