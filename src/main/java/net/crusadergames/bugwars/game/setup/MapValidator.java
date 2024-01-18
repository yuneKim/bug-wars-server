package net.crusadergames.bugwars.game.setup;

import net.crusadergames.bugwars.game.entity.Bug;
import net.crusadergames.bugwars.game.entity.Direction;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class MapValidator {

    private final String[] lines;
    private final String titleLine;
    List<Bug> bugs = new ArrayList<>();
    Set<Integer> swarms = new HashSet<>();
    private int width;
    private int height;

    public MapValidator(String[] mapStrings) {
        this.titleLine = mapStrings[0];

        this.lines = new String[mapStrings.length - 1];
        System.arraycopy(mapStrings, 1, lines, 0, mapStrings.length - 1);
    }

    public boolean validate() {
        return titleLineOk() &&
                correctLineCount() &&
                mapContentIsOk() &&
                bugSpawnsReflectedAboutCenterPoint() &&
                mapObjectsReflectedAboutXAxis(lines) &&
                mapObjectsReflectedAboutXAxis(rotateLines(lines));
    }

    private boolean titleLineOk() {
        String[] tokens = titleLine.split(",");
        try {
            width = Integer.parseInt(tokens[0].trim());
            height = Integer.parseInt(tokens[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("Title line bad");
            return false;
        }
        return true;
    }

    private boolean correctLineCount() {
        if (lines.length != height) {
            System.err.println("Line count incorrect");
            return false;
        }
        return true;
    }

    private boolean mapContentIsOk() {
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y].trim();
            if (line.length() != width) {
                System.err.println("Incorrect line width");
                return false;
            }

            if (line.charAt(0) != 'X' || line.charAt(line.length() - 1) != 'X') {
                System.err.println("Side wall missing");
                return false;
            }

            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (!List.of('X', 'a', '0', '1', '2', '3', ' ').contains(c)) {
                    System.err.println("Invalid character");
                    return false;
                }

                if (y == 0 || y == lines.length - 1) {
                    if (c != 'X') {
                        System.err.println("Top or bottom wall missing");
                        return false;
                    }
                }

                if (List.of('0', '1', '2', '3').contains(c)) {
                    Bug newBug = new Bug();
                    int swarm = Character.getNumericValue(c);
                    swarms.add(swarm);
                    newBug.setCoords(new Point(x, y));
                    newBug.setSwarm(swarm);
                    newBug.setDirection(Direction.EAST);
                    bugs.add(newBug);
                }
            }
        }
        return true;
    }

    private boolean bugSpawnsReflectedAboutCenterPoint() {
        double centerY = height / 2.0 - .5;
        double centerX = width / 2.0 - .5;
        Map<Double, List<Bug>> distanceBuckets = new HashMap<>();
        for (Bug bug : bugs) {
            Double distanceToCenter = Math.sqrt(Math.pow(bug.getCoords().x - centerX, 2) +
                    Math.pow(bug.getCoords().y - centerY, 2));

            if (distanceBuckets.containsKey(distanceToCenter)) {
                distanceBuckets.get(distanceToCenter).add(bug);
            } else {
                distanceBuckets.put(distanceToCenter, new ArrayList<>(List.of(bug)));
            }
        }

        for (Map.Entry<Double, List<Bug>> entry : distanceBuckets.entrySet()) {
            List<Bug> bugList = entry.getValue();
            Map<Integer, Integer> bugCounts = bugList.stream()
                    .collect(Collectors.<Bug, Integer, Integer>toMap(Bug::getSwarm, (bug) -> 1, Integer::sum));

            if (bugCounts.keySet().size() != swarms.size() || bugCounts.values().stream().distinct().count() > 1) {
                System.err.println("Bug spawns not reflected properly and/or unequal swarm sizes");
                return false;
            }
        }

        return true;
    }

    private boolean mapObjectsReflectedAboutXAxis(String[] lines) {
        boolean reflectionGood = true;
        boolean twistedReflectionGood = true;
        for (int y = 0; y < lines.length / 2; y++) {
            String topLine = lines[y].replaceAll("\\d", " ").trim();
            String bottomLine = lines[lines.length - 1 - y].replaceAll("\\d", " ").trim();

            if (!topLine.equals(bottomLine)) {
                reflectionGood = false;
            }

            if (!topLine.contentEquals(new StringBuilder(bottomLine).reverse())) {
                twistedReflectionGood = false;
            }
        }
        if (!reflectionGood && !twistedReflectionGood)
            System.err.println("Map not symmetrical");

        return reflectionGood || twistedReflectionGood;
    }

    private String[] rotateLines(String[] lines) {
        String[] rotated = new String[lines[0].length()];
        for (int x = 0; x < lines[0].length(); x++) {
            StringBuilder newLine = new StringBuilder();
            for (String line : lines) {
                newLine.append(line.charAt(x));
            }
            rotated[x] = String.valueOf(newLine.reverse());
        }
        return rotated;
    }
}
