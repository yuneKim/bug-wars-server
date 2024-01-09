package net.crusadergames.bugwars.game;

import net.crusadergames.bugwars.game.entity.Bug;
import net.crusadergames.bugwars.game.entity.Entity;

import java.util.*;

public class TurnOrderCalculator {

    private final Entity[][] grid;
    private final List<Swarm> swarms;
    private double centerY;
    private double centerX;

    public TurnOrderCalculator(Entity[][] grid, List<Swarm> swarms) {
        this.grid = grid;
        this.swarms = swarms;
    }

    public List<Bug> order() {
        centerY = grid.length / 2.0 - .5;
        centerX = grid[0].length / 2.0 - .5;

        Map<Double, List<Bug>> distanceBuckets = groupBugsByDistance();
        List<Bug> roughOrder = getRoughOrder(distanceBuckets);

        return getSortedOrder(roughOrder);
    }

    private Map<Double, List<Bug>> groupBugsByDistance() {
        Map<Double, List<Bug>> distanceBuckets = new HashMap<>();

        for (Entity[] entities : grid) {
            for (Entity e : entities) {
                if (e == null || e.getClass() != Bug.class) continue;
                Bug bug = (Bug) e;
                Double distanceToCenter = Math.sqrt(Math.pow(bug.getCoords().x - centerX, 2) +
                        Math.pow(bug.getCoords().y - centerY, 2));
                if (distanceBuckets.containsKey(distanceToCenter)) {
                    distanceBuckets.get(distanceToCenter).add(bug);
                } else {
                    List<Bug> newList = new ArrayList<>();
                    newList.add(bug);
                    distanceBuckets.put(distanceToCenter, newList);
                }
            }
        }

        return distanceBuckets;
    }

    private List<Bug> getRoughOrder(Map<Double, List<Bug>> distanceBuckets) {
        List<Bug> turnOrder = new ArrayList<>();
        List<Double> order = new ArrayList<>(distanceBuckets.keySet());
        order.sort(Collections.reverseOrder());
        for (Double distance : order) {
            addToTurnOrder(distanceBuckets, distance, turnOrder);
        }

        return turnOrder;
    }

    private void addToTurnOrder(Map<Double, List<Bug>> distanceBuckets, Double distance, List<Bug> turnOrder) {
        List<Bug> bugs = distanceBuckets.get(distance);
        if (bugs.size() == 2) {
            bugs.sort(Comparator.comparingInt(Bug::getSwarm));
            turnOrder.addAll(distanceBuckets.get(distance));
        } else {
            for (int i = 0; i < bugs.size(); i++) {
                findPair(turnOrder, bugs, i);
            }
        }
    }

    private void findPair(List<Bug> turnOrder, List<Bug> bugs, int i) {
        Bug a = bugs.get(i);
        if (turnOrder.contains(a)) return;
        boolean bugAdded = false;
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
                bugAdded = true;
            }
        }
        if (!bugAdded) turnOrder.add(a);
    }

    private List<Bug> getSortedOrder(List<Bug> turnOrder) {
        List<Bug> sortedTurnOrder = new ArrayList<>();
        int i = 0;
        while (turnOrder.size() != sortedTurnOrder.size()) {
            Bug bug = turnOrder.get(i);
            if (sortedTurnOrder.contains(bug)) {
                i++;
                continue;
            }
            if (sortedTurnOrder.size() % swarms.size() == bug.getSwarm()) {
                sortedTurnOrder.add(bug);
                i++;
                continue;
            }
            for (int j = i + 1; j < turnOrder.size(); j++) {
                Bug bug2 = turnOrder.get(j);
                if (!sortedTurnOrder.contains(bug2) && sortedTurnOrder.size() % swarms.size() == bug2.getSwarm()) {
                    sortedTurnOrder.add(bug2);
                    break;
                }
            }
        }
        return sortedTurnOrder;
    }
}
