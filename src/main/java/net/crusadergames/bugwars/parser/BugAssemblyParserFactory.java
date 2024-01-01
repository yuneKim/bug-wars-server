package net.crusadergames.bugwars.parser;

import org.springframework.stereotype.Component;

@Component
public class BugAssemblyParserFactory {
    public BugAssemblyParser createInstance() {
        return new BugAssemblyParser(BugAssemblyCommands.getActions(),
                BugAssemblyCommands.getControls());
    }
}
