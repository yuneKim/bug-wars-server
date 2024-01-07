package net.crusadergames.bugwars.game.entity;

import java.awt.*;

public interface Entity {
    Point getCoords();

    void setCoords(Point coords);

    String toString();
}
