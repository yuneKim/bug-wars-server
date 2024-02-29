package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseDTO;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BugAssemblyParserService {

    private final BugAssemblyParserFactory bugAssemblyParserFactory;

    public BugAssemblyParserService(BugAssemblyParserFactory bugAssemblyParserFactory) {
        this.bugAssemblyParserFactory = bugAssemblyParserFactory;
    }

    public List<Integer> parse(BugAssemblyParseDTO bugAssemblyParseDTO) throws BugAssemblyParseException {
        BugAssemblyParser parser = bugAssemblyParserFactory.createInstance();

        return parser.parse(bugAssemblyParseDTO.getCode());
    }
}
