package net.crusadergames.bugwars.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class Wall implements Entity {
    private Point coords;

    @Override
    public String toString() {
        return "X";
    }
}
