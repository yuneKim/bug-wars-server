package net.crusadergames.bugwars.game.setup;

import net.crusadergames.bugwars.game.Battleground;
import net.crusadergames.bugwars.game.GameInitializationException;
import net.crusadergames.bugwars.game.Swarm;
import net.crusadergames.bugwars.game.entity.*;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BattlegroundFactory {
    private final Resource mapFile;
    private final List<Swarm> swarms;

    private String mapName;
    private Entity[][] grid;

    public BattlegroundFactory(Resource mapFile, List<Swarm> swarms) {
        this.mapFile = mapFile;
        this.swarms = swarms;
    }

    public Battleground createBattleground() {
        loadMapFromFile();
        List<Bug> bugs = new TurnOrderCalculator(grid, swarms).order();
        return new Battleground(mapName, grid, bugs);
    }

    private void loadMapFromFile() {
        String[] lines;
        try {
            lines = mapFile.getContentAsString(StandardCharsets.UTF_8).split("\\R");
        } catch (IOException e) {
            throw new GameInitializationException("Failed to load map file.");
        }
        String[] infoLine = lines[0].split(",");
        mapName = infoLine[2].trim();
        int height = Integer.parseInt(infoLine[1]);
        int width = Integer.parseInt(infoLine[0]);
        grid = constructGrid(height, width, lines);
    }

    private Entity[][] constructGrid(int height, int width, String[] lines) {
        Entity[][] grid = new Entity[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point point = new Point(x, y);
                char c = lines[y + 1].charAt(x);
                if (c == 'X') {
                    grid[y][x] = new Wall();
                } else if (c == 'a') {
                    grid[y][x] = new Food();
                } else if (List.of('0', '1', '2', '3').contains(c)) {
                    int swarm = Character.getNumericValue(c);
                    if (swarms.size() <= swarm) continue;

                    Bug bug = new Bug(
                            grid,
                            point,
                            swarm,
                            swarms.get(swarm).getBytecode(),
                            Direction.faceCenter(point, height, width)
                    );
                    grid[y][x] = bug;
                }
            }
        }

        return grid;
    }
}
