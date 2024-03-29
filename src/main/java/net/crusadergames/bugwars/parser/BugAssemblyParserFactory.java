package net.crusadergames.bugwars.parser;

import net.crusadergames.bugwars.config.BugAssemblyCommands;
import org.springframework.stereotype.Component;

@Component
public class BugAssemblyParserFactory {
    public BugAssemblyParser createInstance() {
        return new BugAssemblyParser(BugAssemblyCommands.getActions(),
                BugAssemblyCommands.getControls());
    }
}
