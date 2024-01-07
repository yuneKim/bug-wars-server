package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.CreateScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin
@RequestMapping("api/scripts")
@PreAuthorize("hasRole('USER')")
@RestController
public class ScriptController {

    @Autowired
    ScriptService scriptService;

    @GetMapping
    public List<Script> list(Principal principal) {
        return scriptService.getUserScripts(principal);
    }

    @GetMapping(path="/{id}")
    public Script getScript(@PathVariable long id, Principal principal) {
        return scriptService.getScript(id, principal);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Script createScript(@RequestBody CreateScriptRequest request, Principal principal) {
        return scriptService.createScript(request, principal);
    }

}
