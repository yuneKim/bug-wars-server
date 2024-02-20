package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseRequest;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BugAssemblyParserService {

    private final BugAssemblyParserFactory bugAssemblyParserFactory;

    public BugAssemblyParserService(BugAssemblyParserFactory bugAssemblyParserFactory) {
        this.bugAssemblyParserFactory = bugAssemblyParserFactory;
    }

    public List<Integer> parse(BugAssemblyParseRequest bugAssemblyParseRequest) throws BugAssemblyParseException {
        BugAssemblyParser parser = bugAssemblyParserFactory.createInstance();

        return parser.parse(bugAssemblyParseRequest.getCode());
    }
}
