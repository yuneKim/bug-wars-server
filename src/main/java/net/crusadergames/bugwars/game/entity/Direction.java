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

        if (slope > -1 && slope < 1) {
            return coords.x > centerY ? Direction.WEST : Direction.EAST;
        }
        return coords.y > centerY ? Direction.NORTH : Direction.SOUTH;
    }

    public Point forward(Point position) {
        return switch (this) {
            case NORTH -> new Point(position.x, position.y - 1);
            case WEST -> new Point(position.x - 1, position.y);
            case EAST -> new Point(position.x + 1, position.y);
            case SOUTH -> new Point(position.x, position.y + 1);
        };
    }

    public Direction turnLeft() {
        return switch (this) {
            case NORTH -> EAST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
        };
    }

    public Direction turnRight() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            case SOUTH -> EAST;
        };
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
