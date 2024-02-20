package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseRequest;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.service.BugAssemblyParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/parse")
public class BugAssemblyParserController {

    private final BugAssemblyParserService bugAssemblyParserService;

    public BugAssemblyParserController(BugAssemblyParserService bugAssemblyParserService) {
        this.bugAssemblyParserService = bugAssemblyParserService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> parse(@RequestBody BugAssemblyParseRequest bugAssemblyParseRequest) {
        try {
            return bugAssemblyParserService.parse(bugAssemblyParseRequest);
        } catch (BugAssemblyParseException e) {
            // 422 response
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

}
