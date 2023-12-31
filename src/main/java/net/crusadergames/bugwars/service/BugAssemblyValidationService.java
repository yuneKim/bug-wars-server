package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.BugAssemblyValidationRequest;
import net.crusadergames.bugwars.parser.BugAssemblyCommands;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BugAssemblyValidationService {
    public List<Integer> parse(BugAssemblyValidationRequest bugAssemblyValidationRequest) {
        BugAssemblyParser parser = new BugAssemblyParser(BugAssemblyCommands.getActions(),
                BugAssemblyCommands.getControls());

        try {
            return parser.parse(bugAssemblyValidationRequest.getCode());
        } catch (BugAssemblyParseException e) {
            // 422 response
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }
}
