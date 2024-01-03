package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
    public Script getScript(@PathVariable long id) {
        return scriptService.getById(id);
    }

}
