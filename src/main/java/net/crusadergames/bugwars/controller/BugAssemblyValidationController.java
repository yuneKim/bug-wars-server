package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.BugAssemblyValidationRequest;
import net.crusadergames.bugwars.service.BugAssemblyValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/parse")
public class BugAssemblyValidationController {

    @Autowired
    BugAssemblyValidationService bugAssemblyValidationService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> parse(@RequestBody BugAssemblyValidationRequest bugAssemblyValidationRequest) {
        return bugAssemblyValidationService.parse(bugAssemblyValidationRequest);
    }

}
