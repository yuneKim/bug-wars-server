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
    private Map<Integer, Action> actions = new HashMap<>();
    private Map<Integer, Control> controls = new HashMap<>();
    private String name;
    private int swarm;
    private int[] bytecode = {13, 30, 6, 11, 35, 0, 14, 35, 0};
    private int index = 0;
    private boolean bool = false;
    private Point coords;
    private Direction direction;

    public Bug(Point coords, int swarm, Direction direction) {
        this.coords = coords;
        this.swarm = swarm;
        this.direction = direction;
        init();
    }

    public void takeTurn() {
        if (controls.containsKey(bytecode[index])) {
            bool = !bool;
            if (controls.get(bytecode[index]).call()) {
                index = bytecode[index + 1];
                takeTurn();
            } else {
                increment(2);
                takeTurn();
            }
        } else if (actions.containsKey(bytecode[index])) {
            actions.get(bytecode[index]).run();
            increment(1);
        } else {
            throw new RuntimeException("Invalid instruction: " + bytecode[index]);
        }
    }

    private void init() {
        actions.put(0, this::noop);
        actions.put(10, this::mov);
        actions.put(11, this::rotr);
        actions.put(12, this::rotl);
        actions.put(13, this::att);
        actions.put(14, this::eat);

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

    private void noop() {
        System.out.println("noop");
    }

    private void mov() {
        System.out.println("mov");
    }

    private void rotr() {
        System.out.println("rotr");
    }

    private void rotl() {
        System.out.println("rotl");
    }

    private void att() {
        System.out.println("att");
    }

    private void eat() {
        System.out.println("eat");
    }

    private boolean ifEnemy() {
        System.out.println("ifEnemy");
        return bool;
    }

    private boolean ifAlly() {
        System.out.println("ifAlly");
        return bool;
    }

    private boolean ifFood() {
        System.out.println("ifFood");
        return bool;
    }

    private boolean ifEmpty() {
        System.out.println("ifEmpty");
        return bool;
    }

    private boolean ifWall() {
        System.out.println("ifWall");
        return bool;
    }

    private boolean _goto() {
        bool = !bool;
        System.out.println("goto");
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
    interface Action {
        void run();
    }

    @FunctionalInterface
    interface Control {
        boolean call();
    }
}
