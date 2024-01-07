package net.crusadergames.bugwars.game;

import lombok.Data;
import net.crusadergames.bugwars.game.entity.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

@Data
public class GameMap {
    private String name;
    private Entity[][] grid;
    private List<Swarm> swarms = new ArrayList<>();

    public static GameMap load(String path, ResourceLoader loader) {
        GameMap map = new GameMap();

        try {
            Resource resource = loader.getResource(path);
            String[] lines = resource.getContentAsString(StandardCharsets.UTF_8).split("\n");

            String[] infoLine = lines[0].split(",");
            map.setName(infoLine[2]);
            int height = Integer.parseInt(infoLine[1]);
            int width = Integer.parseInt(infoLine[0]);
            Entity[][] grid = new Entity[height][width];
            Map<Integer, List<Bug>> swarmMap = new HashMap<>();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Point point = new Point(x, y);
                    char c = lines[y + 1].charAt(x);
                    if (List.of('X', 'x').contains(c)) {
                        grid[y][x] = new Wall(point);
                    } else if (List.of('A', 'a').contains(c)) {
                        grid[y][x] = new Food(point);
                    } else if (List.of('0', '1', '2', '3').contains(c)) {
                        int swarm = Character.getNumericValue(c);
                        Bug bug = new Bug(
                                point,
                                swarm,
                                Direction.faceCenter(point, height, width)
                        );
                        grid[y][x] = bug;
                        if (swarmMap.containsKey(swarm)) {
                            swarmMap.get(swarm).add(bug);
                        } else {
                            List<Bug> newList = new ArrayList<>();
                            newList.add(bug);
                            swarmMap.put(Character.getNumericValue(c), newList);
                        }

                    }
                }
            }

            double centerX = width / 2.0 - .5;
            double centerY = height / 2.0 - .5;

            Map<Double, List<Bug>> distanceBuckets = new HashMap<>();
            for (Map.Entry<Integer, List<Bug>> entry : swarmMap.entrySet()) {
                for (Bug bug : entry.getValue()) {
                    Double distanceToCenter = Math.sqrt(Math.pow(bug.getCoords().x - centerX, 2) + Math.pow(bug.getCoords().y - centerY, 2));
                    if (distanceBuckets.containsKey(distanceToCenter)) {
                        distanceBuckets.get(distanceToCenter).add(bug);
                    } else {
                        List<Bug> newList = new ArrayList<>();
                        newList.add(bug);
                        distanceBuckets.put(distanceToCenter, newList);
                    }
                }
            }

            List<Bug> turnOrder = new ArrayList<>();
            List<Double> order = new ArrayList<>(distanceBuckets.keySet());
            order.sort(Collections.reverseOrder());
            for (Double distance : order) {
                List<Bug> bugs = distanceBuckets.get(distance);
                if (bugs.size() == 2) {
                    bugs.sort(Comparator.comparingInt(Bug::getSwarm));
                    turnOrder.addAll(distanceBuckets.get(distance));
                } else {
                    for (int i = 0; i < bugs.size(); i++) {
                        Bug a = bugs.get(i);
                        if (turnOrder.contains(a)) continue;
                        for (int j = 1; j < bugs.size(); j++) {
                            Bug b = bugs.get(j);
                            if (turnOrder.contains(b)) continue;

                            double slope = a.getCoords().x == b.getCoords().x
                                    ? 100
                                    : (double) (a.getCoords().y - b.getCoords().y) / (a.getCoords().x - b.getCoords().x);

                            if (centerY == slope * (centerX - a.getCoords().x) + a.getCoords().y ||
                                    (slope == 100 && a.getCoords().x == centerX)) {
                                List<Bug> sortedBugs = new ArrayList<>(List.of(a, b));
                                sortedBugs.sort(Comparator.comparingInt(Bug::getSwarm));
                                turnOrder.addAll(sortedBugs);
                            }
                        }
                    }
                }
            }
            for (Bug bug : turnOrder) {
                System.out.format("%s, %s %s%n", bug.getSwarm(), bug, bug.getCoords());
            }

            for (Double distance : order) {
                List<Bug> bugs = distanceBuckets.get(distance);
                for (Bug bug : bugs) {
                    if (!turnOrder.contains(bug)) {
                        System.out.format("Missing: %s, %s %s Distance: %s%n", bug.getSwarm(), bug, bug.getCoords(), distance);
                    }
                }

            }

            System.out.println(distanceBuckets);
            System.out.println(turnOrder.size());
            List<Bug> sortedTurnOrder = getSortedBugs(turnOrder);
            System.out.println(sortedTurnOrder);
            System.out.println(sortedTurnOrder.size());

            map.setGrid(grid);
            return map;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    private static List<Bug> getSortedBugs(List<Bug> turnOrder) {
        List<Bug> sortedTurnOrder = new ArrayList<>();
        int i = 0;
        while (i < turnOrder.size()) {
            Bug bug = turnOrder.get(i);
            if (sortedTurnOrder.contains(bug)) {
                i++;
                continue;
            }
            if (sortedTurnOrder.size() % 4 == bug.getSwarm()) {
                sortedTurnOrder.add(bug);
                i++;
                continue;
            }
            for (int j = i + 1; j < turnOrder.size(); j++) {
                Bug bug2 = turnOrder.get(j);
                if (!sortedTurnOrder.contains(bug2) && sortedTurnOrder.size() % 4 == bug2.getSwarm()) {
                    sortedTurnOrder.add(bug2);
                    break;
                }
            }
        }
        return sortedTurnOrder;
    }

    public GameMap createSwarm(String name, int[] bytecode) {
        swarms.add(new Swarm(name, bytecode));
        return this;
    }

    public void print() {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Entity e = grid[y][x];
                if (e == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(grid[y][x]);
                }
            }
            System.out.println();
        }
    }
}
