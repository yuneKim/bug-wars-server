package net.crusadergames.bugwars.game.entity;

import java.awt.*;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Direction faceCenter(Point coords, int height, int width) {
        double centerX = width / 2.0 - .5;
        double centerY = height / 2.0 - .5;
        double slope = coords.x == centerX ? 100 : -1 * (centerY - coords.y) / (centerX - coords.x);

        double distance = Math.sqrt(Math.pow(coords.x - centerX, 2) + Math.pow(coords.y - centerY, 2));

        if (slope > -1 && slope < 1) {
            return coords.x > centerY ? Direction.WEST : Direction.EAST;
        }
        return coords.y > centerY ? Direction.NORTH : Direction.SOUTH;
    }

    @Override
    public String toString() {
        return switch (this) {
            case NORTH -> "↑";
            case WEST -> "←";
            case EAST -> "→";
            case SOUTH -> "↓";
        };
    }
}
