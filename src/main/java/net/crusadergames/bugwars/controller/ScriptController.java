package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.ModifyScriptRequest;
import net.crusadergames.bugwars.dto.response.ScriptName;
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

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public List<ScriptName> getAllNamesOfValidScripts() {
        return scriptService.getAllNamesOfValidScripts();
    }

    @GetMapping
    public List<Script> getUserScripts(Principal principal) {
        return scriptService.getUserScripts(principal);
    }

    @GetMapping(path = "/{id}")
    public Script getScript(@PathVariable long id, Principal principal) {
        return scriptService.getScript(id, principal);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Script createScript(@RequestBody ModifyScriptRequest request, Principal principal) {
        return scriptService.createScript(request, principal);
    }

    @PutMapping(path = "/{scriptId}")
    public Script updateScript(@PathVariable Long scriptId, Principal principal, @RequestBody ModifyScriptRequest request) {
        return scriptService.updateScript(scriptId, principal, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{scriptId}")
    public void deleteScriptById(@PathVariable long scriptId, Principal principal) {
        scriptService.deleteScriptById(scriptId, principal);
    }

}
