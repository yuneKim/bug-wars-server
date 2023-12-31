package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseRequest;
import net.crusadergames.bugwars.service.BugAssemblyParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/parse")
public class BugAssemblyParserController {

    @Autowired
    BugAssemblyParserService bugAssemblyParserService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> parse(@RequestBody BugAssemblyParseRequest bugAssemblyParseRequest) {
        return bugAssemblyParserService.parse(bugAssemblyParseRequest);
    }

}
