package net.crusadergames.bugwars.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bug implements Entity, Attackable {

    private Map<Integer, Control> controls = new HashMap<>();

    private Entity[][] map;
    private int swarm;
    private int[] bytecode;
    private int index = 0;
    private boolean bool = true;
    private Point coords;
    private Direction direction;

    public Bug(Entity[][] map, Point coords, int swarm, int[] bytecode, Direction direction) {
        this.map = map;
        this.coords = coords;
        this.swarm = swarm;
        this.bytecode = bytecode;
        this.direction = direction;

        init();
    }

    public int getAction(Entity frontEntity) {
        int result = -1;
        if (controls.containsKey(bytecode[index])) {
            if (controls.get(bytecode[index]).call(frontEntity)) {
                index = bytecode[index + 1];
            } else {
                increment(2);
            }
            return getAction(frontEntity);
        } else {
            result = bytecode[index];
            increment(1);
        }
        return result;
    }

    private void init() {
        controls.put(30, this::ifEnemy);
        controls.put(31, this::ifAlly);
        controls.put(32, this::ifFood);
        controls.put(33, this::ifEmpty);
        controls.put(34, this::ifWall);
        controls.put(35, this::_goto);
    }

    private void increment(int n) {
        index = (index + n) % bytecode.length;
    }


    private boolean ifEnemy(Entity frontEntity) {
        return frontEntity instanceof Bug && ((Bug) frontEntity).getSwarm() != swarm;
    }

    private boolean ifAlly(Entity frontEntity) {
        return frontEntity instanceof Bug && ((Bug) frontEntity).getSwarm() == swarm;
    }

    private boolean ifFood(Entity frontEntity) {
        return frontEntity instanceof Food;
    }

    private boolean ifEmpty(Entity frontEntity) {
        return frontEntity == null;
    }

    private boolean ifWall(Entity frontEntity) {
        return frontEntity instanceof Wall;
    }

    private boolean _goto(Entity frontEntity) {
        return true;
    }

    @Override
    public String toString() {
        String dir = direction.toString();
        return switch (swarm) {
            case 0 -> wrapDir("\033[0;34m", dir); // blue
            case 1 -> wrapDir("\033[0;31m", dir); // red
            case 2 -> wrapDir("\033[0;33m", dir); // yellow
            case 3 -> wrapDir("\033[0;32m", dir); // green
            default -> throw new IllegalStateException("Unexpected value: " + swarm);
        };
    }

    private String wrapDir(String color, String dir) {
        return String.format("%s%s%s", color, dir, "\033[0m");
    }


    @FunctionalInterface
    interface Control {
        boolean call(Entity frontEntity);
    }
}
