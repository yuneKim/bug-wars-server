package net.crusadergames.bugwars.config;

import java.util.HashMap;
import java.util.Map;

public class BugAssemblyCommands {
    public static Map<String, Integer> getActions() {
        Map<String, Integer> commands = new HashMap<>();

        commands.put("noop", 0);
        commands.put("mov", 10);
        commands.put("rotr", 11);
        commands.put("rotl", 12);
        commands.put("att", 13);
        commands.put("eat", 14);

        return commands;
    }

    public static Map<String, Integer> getControls() {
        Map<String, Integer> commands = new HashMap<>();

        commands.put("ifEnemy", 30);
        commands.put("ifAlly", 31);
        commands.put("ifFood", 32);
        commands.put("ifEmpty", 33);
        commands.put("ifWall", 34);
        commands.put("goto", 35);

        return commands;
    }
}
