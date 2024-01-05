package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
