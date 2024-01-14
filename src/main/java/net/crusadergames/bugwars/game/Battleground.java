package net.crusadergames.bugwars.game;

import lombok.Data;
import net.crusadergames.bugwars.annotation.ExcludeFromJacocoGeneratedReport;
import net.crusadergames.bugwars.game.entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Battleground {
    private final List<Bug> bugs;
    private String name;
    private Entity[][] grid;
    private Map<Integer, Action> actions = new HashMap<>();
    private int index;

    public Battleground(String name, Entity[][] grid, List<Bug> bugs) {
        this.name = name;
        this.grid = grid;
        this.bugs = bugs;

        init();
    }

    public List<ActionSummary> nextTick() {
        List<ActionSummary> actionsTaken = new ArrayList<>();
        for (index = 0; index < bugs.size(); index++) {
            Bug bug = bugs.get(index);
            Point bugFrontCoords = bug.getDirection().forward(bug.getCoords());
            int action = bug.getAction(getEntityAtCoords(bugFrontCoords));
            if (!actions.containsKey(action)) throw new RuntimeException("Invalid action.");

            actionsTaken.add(new ActionSummary(bug.getCoords(), action));
            actions.get(action).run(bug);
        }
        return actionsTaken;
    }

    @ExcludeFromJacocoGeneratedReport
    public void print() {
        for (Entity[] entities : grid) {
            for (Entity e : entities) {
                if (e == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(e);
                }
            }
            System.out.println();
        }
    }

    private void init() {
        actions.put(0, this::noop);
        actions.put(10, this::mov);
        actions.put(11, this::rotr);
        actions.put(12, this::rotl);
        actions.put(13, this::att);
        actions.put(14, this::eat);
    }

    private void noop(Bug bug) {
        // do nothing!
    }

    private void mov(Bug bug) {
        Point bugFrontCoords = bug.getDirection().forward(bug.getCoords());
        Entity destination = getEntityAtCoords(bugFrontCoords);
        if (destination != null) return;

        grid[bugFrontCoords.y][bugFrontCoords.x] = bug;
        grid[bug.getCoords().y][bug.getCoords().x] = null;
        bug.setCoords(bugFrontCoords);
    }

    private void rotr(Bug bug) {
        bug.setDirection(bug.getDirection().turnRight());
    }

    private void rotl(Bug bug) {
        bug.setDirection(bug.getDirection().turnLeft());
    }

    private void att(Bug bug) {
        Point bugFrontCoords = bug.getDirection().forward(bug.getCoords());
        Entity target = getEntityAtCoords(bugFrontCoords);
        if (!(target instanceof Attackable)) return;

        if (target instanceof Bug) {
            if (bugs.indexOf(target) < index) index--;
            bugs.remove(target);
            grid[bugFrontCoords.y][bugFrontCoords.x] = new Food();
        } else if (target instanceof Food) {
            grid[bugFrontCoords.y][bugFrontCoords.x] = null;
        }
    }

    private void eat(Bug bug) {
        Point bugFrontCoords = bug.getDirection().forward(bug.getCoords());
        Entity target = getEntityAtCoords(bugFrontCoords);
        if (!(target instanceof Food)) return;

        Bug newSpawn = new Bug(
                bugFrontCoords,
                bug.getSwarm(),
                bug.getBytecode(),
                Direction.faceCenter(bugFrontCoords, grid.length, grid[0].length)
        );

        grid[bugFrontCoords.y][bugFrontCoords.x] = newSpawn;
        bugs.add(index, newSpawn);
        index++;
    }

    private Entity getEntityAtCoords(Point coords) {
        return grid[coords.y][coords.x];
    }

    @FunctionalInterface
    interface Action {
        void run(Bug bug);
    }
}
